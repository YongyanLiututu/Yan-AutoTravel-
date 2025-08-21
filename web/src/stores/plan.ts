import { defineStore } from 'pinia'
import axios from 'axios'

export type PlanRow = { day: number; date: string; morning?: string; noon?: string; afternoon?: string; evening?: string; notes?: string }
export type PlanData = { id?: string; title?: string; city?: string; days?: number; rows: PlanRow[] }

type ThumbIndex = Record<string, string>

function uid(){ return Math.random().toString(36).slice(2, 10) }

export const usePlanStore = defineStore('plan', {
  state: () => ({
    plan: { rows: [] } as PlanData,
    sessionId: localStorage.getItem('ym-session-id') || uid(),
    thumbs: {} as ThumbIndex,
  }),
  actions: {
    ensureSession(){
      if (!this.sessionId){ this.sessionId = uid(); localStorage.setItem('ym-session-id', this.sessionId) }
      else { localStorage.setItem('ym-session-id', this.sessionId) }
    },
    async saveDraft(){
      this.ensureSession()
      try{
        const { data } = await axios.post('/api/drafts', this.plan, { params: { sessionId: this.sessionId } })
        if (data?.id) this.plan.id = data.id
      }catch{
        // fallback: local cache
        const key = `draft-${this.sessionId}`
        localStorage.setItem(key, JSON.stringify(this.plan))
      }
    },
    async loadDraft(id?: string){
      this.ensureSession()
      try{
        if (id){
          const { data } = await axios.get(`/api/drafts/${encodeURIComponent(id)}`, { params: { sessionId: this.sessionId } })
          this.plan = data || { rows: [] }
          return
        }
        const { data } = await axios.get('/api/drafts/latest', { params: { sessionId: this.sessionId } })
        this.plan = data || { rows: [] }
      }catch{
        const key = `draft-${this.sessionId}`
        const raw = localStorage.getItem(key)
        if (raw) this.plan = JSON.parse(raw)
      }
    },
    async loadPlanById(id: string){
      try{
        const { data } = await axios.get(`/api/plan/${encodeURIComponent(id)}`)
        this.plan = data || { rows: [] }
      }catch{
        // fallback: try local if same id was saved
        const raw = localStorage.getItem(`plan-${id}`)
        if (raw) this.plan = JSON.parse(raw)
      }
    },
    async loadThumbsIndex(){
      // optional: served from public root as /thumbs_index.csv
      try{
        const res = await fetch('/thumbs_index.csv', { cache: 'no-store' })
        if (!res.ok) return
        const text = await res.text()
        const lines = text.split(/\r?\n/).filter(Boolean)
        const header = lines.shift() || ''
        const cols = header.split(',').map(x=>x.trim())
        const iUrl = cols.indexOf('origin_url'), iPath = cols.indexOf('thumb_path')
        const map: ThumbIndex = {}
        for (const line of lines){
          const parts = line.split(',')
          const o = parts[iUrl]?.trim(), p = parts[iPath]?.trim()
          if (o && p) map[o] = p.startsWith('/') ? p : `/${p.replace(/\\/g,'/')}`
        }
        this.thumbs = map
      }catch{
        // ignore if not present
      }
    }
  }
})


