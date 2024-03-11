/**
 * Authentication middleware(사용자 인증 미들웨어)
 *
 * @guard to 이동할 url
 * @guard from 현재 url
 * @guard next to에서 지정한 url로 이동하기 위해 호출해야 하는 함수
 *
 * next(): 라우팅 승인
 * next(false): 라우팅 취소
 * next('/'): 특정 라우팅으로 진입
 *
 * 라우팅 허용 여부와 상관없이 반드시 next 호출 필수. 호출하지 않으면 대기 상태.
 *
 * 1. 로그인, 회원가입 페이지는 anonymousCallable 통과
 * 2. 권한이 없는 경우 페이지 통과 불가능
 * 3. 토큰이 없는 경우 로그인 페이지
 * 4. 라우터 변경할 때마다 토큰 체크
 * 5. 마지막 라우트 path 기억해서, 로그인 페이지에서 새로고침시 페이지 이동
 */
import { useUserStore } from '@/store/user'
import { useUrlStore } from '@/store/url.js'

const TOKEN_EXPIRED = import.meta.env.VITE_TOKEN_EXPIRED

export default (to, from, next) => {
  const userStore = useUserStore()
  userStore.init()

  const urlStore = useUrlStore()
  urlStore.init()

  if (to.matched.some((record) => record.meta.anonymousCallable) === true) {
    // 1. 로그인 페이지에 직접 접근 시, 토큰 초기화
    if (userStore.accessToken && userStore.accessToken !== '') {
      userStore.setTokenClear()
    }
    next()
  } else {
    if (userStore.accessToken && userStore.accessToken !== '') {
      let roleStatus = userStore.role // 권한 상태
      if (to.meta.roles && !to.meta.roles.includes(roleStatus)) {
        // 2. 권한이 없는 경우 페이지 통과 불가능
        next({ path: '/home' })
      } else {
        if (to.path === '/') {
          if (urlStore.getLastUrl !== '/login') {
            next({ path: urlStore.getLastUrl })
          }
        } else {
          // 4. 라우터 변경할 때마다 토큰 체크
          userStore.loginExtension().then(() => {})
          userStore.setTokenExpired(TOKEN_EXPIRED)

          urlStore.setLastUrl(to.path)
          next()
        }
      }
    } else {
      // 3. 토큰이 없는 경우 로그인 페이지
      next({ name: 'login' })
    }
  }
}
