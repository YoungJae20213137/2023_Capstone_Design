import { fileURLToPath, URL } from 'url'
import { defineConfig } from 'vite'
import Components from 'unplugin-vue-components/vite'
import { PrimeVueResolver } from 'unplugin-vue-components/resolvers'
import AutoImport from 'unplugin-auto-import/vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // primevue, components 전역 사용을 위한 auto-import
    Components({
      resolvers: [PrimeVueResolver()],
      dts: true,
    }),
    // vue, vue-router 메소드의 전역 사용을 위한 auto-import
    AutoImport({
      include: [/\.[tj]sx?$/, /\.vue$/, /\.vue\?vue/, /\.md$/],
      imports: ['vue', 'vue-router', '@vueuse/core', 'vee-validate'],
      dts: true,
    }),
  ],
  build: {
    outDir: '../backend/src/main/resources/static',
    emptyOutDir: true,
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
})
