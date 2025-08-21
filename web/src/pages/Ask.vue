<template>
  <div class="ask">
    <h2>问答系统</h2>
    <div class="ask-box card-glass">
      <el-input v-model="msg" type="textarea" :rows="3" placeholder="输入你的问题，例如：周末上海一日亲子游，预�?00�?�? />
      <div class="actions">
        <el-button class="is-brand" @click="send" :disabled="loading">发�?/el-button>
        <el-button text @click="clearLogs">清空</el-button>
        <el-upload :show-file-list="false" :before-upload="beforeUpload" accept="image/*">
          <el-button>添加图片</el-button>
        </el-upload>
      </div>
      <div class="log">
        <div v-for="(l,i) in logs" :key="i" class="line">{{ l }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { usePlanStore } from '@/stores/plan'
import axios from 'axios'

const store = usePlanStore()
const msg = ref('')
const logs = ref<string[]>([])
const loading = ref(false)
function send() {
  store.ensureSession()
  const toSend = rewriteIfAmbiguous(msg.value)
  logs.value = []
  loading.value = true
  const es = new EventSource(`/api/qa/ask?sessionId=${encodeURIComponent(store.sessionId)}&q=${encodeURIComponent(toSend)}`, { withCredentials: false } as any)
  // Server sends raw lines prefixed by "data: " in this stub
  es.onmessage = (e) => {
    logs.value.push(e.data)
    if (e.data === 'DONE') { es.close(); loading.value = false }
  }
}
function clearLogs(){ logs.value = [] }

function rewriteIfAmbiguous(text: string){
  const trimmed = (text||'').trim()
  if (trimmed.length < 6 || /^(去|玩|推荐|哪里)/.test(trimmed)){
    // 简单重写：补充更明确意图与偏好
    const city = '上海'
    const days = 2
    return `请根�?{city}�?{days}天周末短途出行，偏好美食和亲子活动，预算中等，生成行程建议并给出理由。问题原文：${trimmed}`
  }
  return trimmed
}

async function beforeUpload(file: File){
  try{
    const fd = new FormData()
    fd.append('file', file)
    const { data } = await axios.post('/api/vision/analyze', fd, { headers: { 'Content-Type': 'multipart/form-data' } })
    const cap = (data?.captions||[]).join('�?)
    const ocr = data?.ocr || ''
    const tags = (data?.tags||[]).join(', ')
    const extra = [`[图片描述:${cap}]`, ocr ? `[OCR:${ocr}]` : '', tags ? `[标签:${tags}]` : ''].filter(Boolean).join(' ')
    msg.value = (msg.value ? msg.value + '\n' : '') + extra
  }catch(e){
    // ignore
  }
  return false
}
</script>

<style scoped>
.mt8 { margin-top: 8px; }
.ask-box{ padding: 12px; }
.actions{ margin: 8px 0; display:flex; gap:8px; align-items:center; }
.log { margin-top: 12px; border: 1px dashed #ddd; padding: 8px; min-height: 120px; background: #fff; border-radius: 8px; }
.line{ padding: 2px 0; }
</style>


