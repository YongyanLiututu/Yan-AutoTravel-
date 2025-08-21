<template>
  <div class="home">
    <section class="hero card-glass">
      <div class="hero-left">
        <h1>你的 AI 行程管家</h1>
        <p class="sub">用几条信息，生成灵感、规划日程、估算路途，让旅行更轻松�?/p>
        <div class="quick">
          <el-form :model="form" label-width="64px">
            <el-form-item label="城市">
              <el-autocomplete v-model="form.city" :fetch-suggestions="fetchCity" placeholder="例如：上�? />
            </el-form-item>
            <el-form-item label="天数">
              <el-input-number v-model="form.days" :min="1" :max="14" />
            </el-form-item>
            <el-form-item label="预算">
              <el-slider v-model="form.budget" :min="0" :max="2" :marks="{0:'�?,1:'�?,2:'�?}" />
            </el-form-item>
            <el-form-item label="主题">
              <div class="chips">
                <el-check-tag v-for="t in themes" :key="t" :checked="form.topics.includes(t)" @change="(v:boolean)=>toggleTopic(t,v)">{{ t }}</el-check-tag>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button class="is-brand" @click="createDraft">一键生成草�?/el-button>
              <el-button @click="goDiscoverWithPref">先看看推�?/el-button>
              <el-button text type="primary" @click="goAsk">快速问�?/el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <div class="hero-right">
        <div class="preview card-glass">
          <div class="badge">实时</div>
          <div class="t1">本周�?· {{ form.city }}</div>
          <div class="t2">为你规划 {{ form.days }} 天轻松游</div>
        </div>
      </div>
    </section>
    
    <section class="features">
      <el-row :gutter="16">
        <el-col :span="8">
          <div class="f card-glass">
            <div class="ft">智能规划�?/div>
            <div class="fs">自动生成可编辑的日程表，支持出行时间估算与提醒�?/div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="f card-glass">
            <div class="ft">灵感与推�?/div>
            <div class="fs">根据城市、主题和偏好，推荐热门路线与冷门好去处�?/div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="f card-glass">
            <div class="ft">对话式问�?/div>
            <div class="fs">使用自然语言快速查询路线、预算、最佳季节等信息�?/div>
          </div>
        </el-col>
      </el-row>
    </section>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { usePlanStore } from '../stores/plan'

const router = useRouter()
const store = usePlanStore()
const themes = ['亲子','自然','美食','网红','人文']
const form = reactive({ city: '上海', days: 2, budget: 1, topics: [] as string[] })

function toggleTopic(t:string, v:boolean){
  if (v && !form.topics.includes(t)) form.topics.push(t)
  if (!v) form.topics = form.topics.filter(x=>x!==t)
}

function fetchCity(query: string, cb: Function){
  axios.get('/api/cities/suggest', { params: { q: query } }).then(({data})=> cb((data||[]).map((x:string)=>({ value: x }))))
}

async function createDraft(){
  store.ensureSession()
  const payload = { city: form.city, days: form.days, budget: ['low','mid','high'][form.budget], themes: form.topics }
  const { data } = await axios.post('/api/drafts', payload, { params: { sessionId: store.sessionId } })
  const id = data?.id
  router.push(`/editor?draftId=${encodeURIComponent(id)}`)
}

function goDiscoverWithPref(){
  const p = new URLSearchParams()
  p.set('city', form.city)
  if (form.topics.length) p.set('theme', form.topics.join(','))
  router.push('/discover?' + p.toString())
}

function goAsk() { router.push('/ask') }
</script>

<style scoped>
.home { max-width: 1120px; margin: 0 auto; }
.hero{ display:flex; gap: 20px; padding: 18px; }
.hero-left{ flex: 1; }
.hero-right{ width: 360px; }
.sub{ color: var(--app-muted); margin: 8px 0 16px; }
.preview{ position: relative; padding: 18px; background: var(--app-bg-soft); border-radius: 12px; }
.badge{ position:absolute; right:12px; top:12px; background:#fff; color: var(--brand-strong); font-weight:700; padding:2px 8px; border-radius:999px; font-size:12px; }
.t1{ font-weight:700; margin-bottom: 4px; }
.t2{ color:#444; }
.features{ margin-top: 20px; }
.f{ padding: 16px; }
.ft{ font-weight: 700; margin-bottom: 6px; }
.fs{ color: #444; }
.chips :deep(.el-check-tag){ margin-right: 8px; }
</style>


