<template>
  <div class="pref-fab card-glass">
    <el-popover placement="left" trigger="click" :width="280">
      <template #reference>
        <el-button circle class="is-brand">偏好</el-button>
      </template>
      <div class="pref-wrap">
        <el-check-tag v-for="p in options" :key="p" :checked="picked.has(p)" @change="(v:boolean)=>toggle(p,v)">{{ p }}</el-check-tag>
        <div class="ops">
          <el-button size="small" @click="save">保存</el-button>
        </div>
      </div>
    </el-popover>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive } from 'vue'
import axios from 'axios'
import { usePlanStore } from '@/stores/plan'

const store = usePlanStore()
const options = ['亲子','少人','自然','预算敏感','爱拍�?]
const picked = reactive(new Set<string>())

onMounted(async ()=>{
  store.ensureSession()
  try{
    const { data } = await axios.get('/api/user/pref', { params: { sessionId: store.sessionId } })
    const v: string[] = data?.values || []
    v.forEach(x=>picked.add(x))
  }catch{}
})

function toggle(p: string, checked: boolean){
  if (checked) picked.add(p); else picked.delete(p)
}
async function save(){
  try{
    await axios.post('/api/user/pref', { values: Array.from(picked) }, { params: { sessionId: store.sessionId } })
  }catch{}
}
</script>

<style scoped>
.pref-fab{ position: fixed; right: 16px; top: 80px; padding: 6px; border-radius: 999px; }
.pref-wrap{ display:flex; gap:8px; flex-wrap: wrap; }
.ops{ margin-top: 8px; text-align: right; width: 100%; }
</style>


