<template>
  <div class="discover">
    <div class="head">
      <h2>发现推荐</h2>
      <el-input v-model="kw" placeholder="输入主题/城市，如：亲�?上海" style="max-width:340px" clearable />
      <el-button class="is-brand" @click="fetchList">搜索</el-button>
    </div>
    <el-row :gutter="16">
      <el-col :span="8" v-for="item in list" :key="item.id">
        <el-card class="mb12 card-glass">
          <template #header>
            <div class="title">{{ item.title }}</div>
          </template>
          <div class="thumb" v-if="thumbOf(item)">
            <img :src="thumbOf(item)" alt="thumb" />
          </div>
          <div class="sub">{{ item.city }} · {{ item.tags.join(' / ') }}</div>
          <el-button text type="primary" @click="goEditor">加入编辑�?/el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import axios from 'axios'
import { onMounted, ref } from 'vue'
import { usePlanStore } from '@/stores/plan'
import { useRouter } from 'vue-router'

const router = useRouter()
const list = ref<any[]>([])
const kw = ref('')
const store = usePlanStore()
async function fetchList(){
  const { data } = await axios.get('/api/recommendations', { params: kw.value ? { q: kw.value } : {} })
  list.value = data
}
onMounted(async ()=>{ await store.loadThumbsIndex(); await fetchList() })

function thumbOf(item:any){
  const url = item?.image_url
  if (!url) return ''
  return store.thumbs[url] || ''
}

function goEditor() { router.push('/editor') }
</script>

<style scoped>
.mb12 { margin-bottom: 12px; }
.title { font-weight: 600; margin-bottom: 6px; }
.sub { color: #666; margin-bottom: 6px; }
.head{ display:flex; gap:10px; align-items:center; margin-bottom:12px; }
.thumb{ margin-bottom: 8px; overflow:hidden; border-radius: 8px; }
.thumb img{ width: 100%; display:block; }
</style>


