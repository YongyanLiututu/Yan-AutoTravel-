<template>
  <div class="editor">
    <h2>行程编辑�?/h2>
    <el-row :gutter="16">
      <el-col :span="16">
        <el-card class="card-glass">
          <template #header>
            <div class="card-hd">行程规划�?/div>
          </template>
          <el-table :data="table" size="small" border stripe row-key="day">
            <el-table-column prop="day" label="�? width="70" />
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="morning" label="上午" />
            <el-table-column prop="noon" label="中午" />
            <el-table-column prop="afternoon" label="下午" />
            <el-table-column prop="evening" label="晚上" />
            <el-table-column prop="notes" label="备注" />
          </el-table>
          <div class="tbl-actions">
            <el-button size="small" class="is-brand" @click="addDay">添加一�?/el-button>
            <el-button size="small" text type="primary" @click="autoFill">AI 生成草案</el-button>
            <el-button size="small" type="primary" @click="save">保存草稿</el-button>
            <el-button size="small" @click="loadLatest">读取最近草�?/el-button>
            <el-popover placement="bottom" title="拖拽排序" :width="320" trigger="click">
              <template #reference>
                <el-button size="small">拖拽排序</el-button>
              </template>
              <div class="drag-wrap">
                <Draggable v-model="table" item-key="day" handle=".drag-handle">
                  <template #item="{element}">
                    <div class="drag-item">
                      <span class="drag-handle">�?/span>
                      <span>�?{{ element.day }} �?· {{ element.date }}</span>
                    </div>
                  </template>
                </Draggable>
              </div>
            </el-popover>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="card-glass">
          <template #header>
            <div class="card-hd">出行助手</div>
          </template>
          <el-form label-width="88px" :model="helper">
            <el-form-item label="出发�?>
              <el-input v-model="helper.origin" placeholder="酒店/地址" />
            </el-form-item>
            <el-form-item label="目的�?>
              <el-input v-model="helper.dest" placeholder="景点/地址" />
            </el-form-item>
            <el-form-item label="交通方�?>
              <el-select v-model="helper.mode" style="width: 100%">
                <el-option label="驾车" value="drive" />
                <el-option label="步行" value="walk" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button class="is-brand" @click="estimate">估算时间</el-button>
            </el-form-item>
          </el-form>
          <div v-if="etaText" class="eta">{{ etaText }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import axios from 'axios'
import { reactive, ref, onMounted } from 'vue'
import dayjs from 'dayjs'
import { usePlanStore, type PlanRow } from '@/stores/plan'
import Draggable from 'vuedraggable'

const store = usePlanStore()
const table = ref<PlanRow[]>([])

onMounted(async ()=>{
  await store.loadDraft()
  if (store.plan?.rows?.length){
    table.value = JSON.parse(JSON.stringify(store.plan.rows))
  }else{
    table.value = [ { day: 1, date: dayjs().format('YYYY-MM-DD'), morning: '抵达与入�?, afternoon: '城市漫步', evening: '美食打卡' } ]
  }
})

function addDay(){
  const next = (table.value[table.value.length - 1]?.day || 0) + 1
  const nextDate = dayjs(table.value[table.value.length - 1]?.date || dayjs()).add(1, 'day').format('YYYY-MM-DD')
  table.value.push({ day: next, date: nextDate })
}

async function autoFill(){
  if (table.value.length < 2) addDay()
  table.value[0].morning = table.value[0].morning || '外滩观光'
  table.value[0].afternoon = table.value[0].afternoon || '南京东路步行�?
  table.value[0].evening = table.value[0].evening || '黄浦江夜�?
}

async function save(){
  store.plan.rows = JSON.parse(JSON.stringify(table.value))
  await store.saveDraft()
}

async function loadLatest(){
  await store.loadDraft()
  table.value = JSON.parse(JSON.stringify(store.plan.rows || []))
}

const helper = reactive({ origin: '', dest: '', mode: 'drive' })
const etaText = ref('')
async function estimate(){
  etaText.value = '计算�?..'
  try{
    const payload = { points: [helper.origin, helper.dest].filter(Boolean).map((x:string)=>({ lon: 121.4737, lat: 31.2304 })) }
    const { data } = await axios.post('/api/route/matrix', payload)
    const mins = Math.round((data?.edges?.[0]?.duration || 900)/60)
    etaText.value = `预计耗时�?${mins} 分钟`
  }catch(e){
    etaText.value = '估算失败，请稍后再试'
  }
}
</script>

<style scoped>
.editor { max-width: 1120px; margin: 0 auto; }
.card-hd{ font-weight: 700; }
.tbl-actions{ display:flex; gap:8px; margin-top:8px; }
.eta{ margin-top: 8px; color:#333; }
.drag-wrap{ max-height: 320px; overflow:auto; }
.drag-item{ display:flex; gap:8px; align-items:center; padding:6px 8px; border:1px dashed #ddd; border-radius:8px; margin-bottom:6px; background:#fff; }
.drag-handle{ cursor:grab; }
</style>


