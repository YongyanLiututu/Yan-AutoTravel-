<template>
  <div class="app-shell">
    <header class="shell-header">
      <div class="inner header-inner">
        <div class="logo">Yan-AutoTravel <span class="brand-chip">TRAVEL</span></div>
        <nav class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/editor">行程编辑�?/router-link>
          <router-link to="/discover">发现推荐</router-link>
          <router-link to="/ask">问答系统</router-link>
        </nav>
        <div>
          <el-switch
            v-model="dark"
            active-text="暗色"
            inactive-text="亮色"
            @change="toggleTheme"
          />
          <PrefSwitch />
        </div>
      </div>
    </header>
    <main class="shell-main">
      <div class="inner">
        <transition name="page" mode="out-in">
          <router-view />
        </transition>
      </div>
    </main>
    <footer class="shell-footer">
      <div class="inner">© 2025 Yan-AutoTravel · Made with �?/div>
    </footer>
  </div>
  
</template>

<script setup lang="ts">
import '@/styles/theme.css'
import { ref, onMounted } from 'vue'
import PrefSwitch from './components/PrefSwitch.vue'

const dark = ref<boolean>(localStorage.getItem('ym-theme') === 'dark')
onMounted(()=>{ applyTheme() })
function toggleTheme(){ dark.value = !dark.value; localStorage.setItem('ym-theme', dark.value ? 'dark' : 'light'); applyTheme() }
function applyTheme(){
  const el = document.documentElement
  if (dark.value) el.setAttribute('data-theme','dark')
  else el.removeAttribute('data-theme')
}
</script>

<style scoped>
.inner{ max-width: 1120px; margin: 0 auto; padding: 10px 16px; }
.header-inner{ display:flex; align-items:center; justify-content:space-between; }
.shell-footer{ color:#fff; opacity:0.9; border-top:1px solid var(--app-border); background: rgba(255,255,255,0.15); }
</style>


