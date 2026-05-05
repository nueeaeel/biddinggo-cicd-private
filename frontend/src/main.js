import { createApp } from 'vue'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { aliases, mdi } from 'vuetify/iconsets/mdi'
import App from './App.vue'
import router from './router'
import { useToast } from './composables/useToast'
import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'
import './styles/app.css'
import './styles/index.css'

const vuetify = createVuetify({
  components,
  directives,
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: { mdi },
  },
})

const app = createApp(App)
const { showToast } = useToast()

app.config.errorHandler = (error) => {
  showToast(error?.message || '예상치 못한 오류가 발생했습니다.', { color: 'error' })
  console.error(error)
}

app.use(router).use(vuetify).mount('#app')
