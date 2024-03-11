import _ from 'lodash'
import router from '@/router/index.js'
import { inject, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useToast } from 'vue-toastification'

const useCrud = () => {
  const $axios = inject('$axios')
  const route = useRoute()
  const toast = useToast()

  /** 리스트 페이지 이동 */
  const onList = async (routerUrl) => {
    await router.push(routerUrl)
  }
  /** 추가 페이지 이동 */
  const onAdd = async (routerUrl) => {
    await router.push(`${routerUrl}/0`)
  }
  /** 수정 페이지 이동 */
  const onEdit = async (routerUrl, selectedList) => {
    if (selectedList.value.length > 1) {
      toast.error('수정할 목록을 한개만 선택해주세요.')
      return
    }
    await router.push(`${routerUrl}/${selectedList.value[0].id}`)
  }
  /** GET API 호출 */
  const onGet = async (apiUrl) => {
    const dataId = route.params.id
    const response = ref()

    if (dataId !== '0') {
      try {
        response.value = await $axios.get(`${apiUrl}/${dataId}`).then((res) => res.data)
      } catch (err) {
        toast.error('목록을 가져오는데 실패했습니다.', err)
      }
    }

    return response.value
  }
  /** common logic */
  const createForm = (params, isMultiPart) => {
    const formData = new FormData()
    const headers = { 'Content-Type': 'application/json' }

    if (isMultiPart) {
      formData.append('image1', params.image1)
      formData.append('image2', params.image2)
      formData.append(
        'data',
        new Blob([JSON.stringify(_.omit(params, ['image1', 'image2']))], { type: 'application/json' }),
      )
      headers['Content-Type'] = 'multipart/form-data'
    } else {
      _.forEach(params, (value, key) => {
        formData.append(key.toString(), value.toString())
      })
    }

    return { formData, headers }
  }
  /** POST API 호출 */
  const onPost = async (apiUrl, routerUrl, params, isMultiPart = false) => {
    const { formData, headers } = createForm(params, isMultiPart)

    await $axios.post(apiUrl, formData, { headers }).then(() => {
      router.push(routerUrl)
      toast.success('항목을 추가했습니다.')
    })
  }
  /** PUT API 호출 */
  const onPut = async (apiUrl, routerUrl, params, isMultiPart = false) => {
    const dataId = route.params.id
    const { formData, headers } = createForm(params, isMultiPart)

    await $axios.put(`${apiUrl}/${dataId}`, formData, { headers }).then(() => {
      router.push(routerUrl)
      toast.success('항목을 수정했습니다.')
    })
  }
  /** DELETE API 호출 */
  const onDelete = async (apiUrl, selectedList) => {
    const deletedIds = _.map(selectedList.value, 'id')
    if (deletedIds.length === 0) {
      toast.error('삭제할 목록을 한개 이상 선택해주세요.')
    } else {
      await $axios
        .delete(`${apiUrl}/${deletedIds}`)
        .then(() => {
          toast.success('항목을 삭제했습니다.')
        })
        .catch((err) => {
          toast.error(err)
        })
    }
  }

  return { onList, onAdd, onEdit, onGet, onPost, onPut, onDelete }
}
export default useCrud
