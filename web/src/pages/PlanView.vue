<template>
  <div class="planview">
    <h2>行程详情</h2>
    <el-card class="card-glass">
      <el-table :data="rows" border stripe size="small">
        <el-table-column prop="day" label="�? width="70" />
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="morning" label="上午" />
        <el-table-column prop="noon" label="中午" />
        <el-table-column prop="afternoon" label="下午" />
        <el-table-column prop="evening" label="晚上" />
        <el-table-column prop="notes" label="备注" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { usePlanStore, type PlanRow } from '@/stores/plan'

const route = useRoute()
const store = usePlanStore()
const rows = ref<PlanRow[]>([])

onMounted(async ()=>{
  const id = String(route.params.id || '')
  if (id) await store.loadPlanById(id)
  rows.value = store.plan.rows || []
})
</script>

<style scoped>
.planview { max-width: 980px; margin: 0 auto; }
</style>


