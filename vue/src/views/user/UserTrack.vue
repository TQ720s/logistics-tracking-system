<template>
  <div class="user-track">
    <h2>物流轨迹查询</h2>
    
    <div class="track-controls">
      <div class="form-group">
        <label>订单号：</label>
        <input v-model="orderNoInput" placeholder="请输入要查询的订单号" @keyup.enter="loadOrderTrack"/>
        <button @click="loadOrderTrack">查询</button>
      </div>
    </div>
    
    <div v-if="!orderDetail && searched" class="no-selection">
      <p>未查询到该订单的物流信息，请检查订单号是否正确。</p>
    </div>
    
    <div v-if="!orderDetail && !searched" class="no-selection">
      <p>请输入订单号以查询物流轨迹</p>
    </div>

    <template v-if="orderDetail">
      <div class="track-info">
        <div class="order-summary">
          <h3>订单信息</h3>
          <div class="summary-item">
            <label>订单号：</label>
            <span>{{ orderDetail.orderNo }}</span>
          </div>
          <div class="summary-item">
            <label>状态：</label>
            <span :class="['status', getStatusClass(orderDetail.status)]">
              {{ orderDetail.status }}
            </span>
          </div>
          <div class="summary-item">
            <label>寄件地址：</label>
            <span>{{ orderDetail.sender?.address }}</span>
          </div>
          <div class="summary-item">
            <label>收件地址：</label>
            <span>{{ orderDetail.receiver?.address }}</span>
          </div>
        </div>
      </div>
      
      <div id="map-container-user" class="map-container">
        <div v-if="mapErrorMessage" class="map-error-overlay">
          <p>{{ mapErrorMessage }}</p>
        </div>
      </div>
      
      <div class="track-timeline">
        <h3>物流轨迹</h3>
        <div class="timeline">
          <div v-for="(track, index) in orderDetail.trackList" :key="track.time" class="timeline-item">
            <div class="timeline-dot"></div>
            <div class="timeline-content">
              <div class="timeline-time">{{ formatDate(track.time) }}</div>
              <div class="timeline-location">{{ track.location }}</div>
              <div class="timeline-status">{{ track.status }}</div>
              <div class="timeline-operator">操作员：{{ track.operator }}</div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const orderNoInput = ref('')
const orderDetail = ref(null)
const searched = ref(false)
const mapContainerId = 'map-container-user'
let map = null
const mapErrorMessage = ref('')

const initMap = async () => {
  await nextTick()
  const mapContainer = document.getElementById(mapContainerId)
  if (!mapContainer) {
    console.error('地图容器未找到')
    return null
  }
  return new AMap.Map(mapContainer, {
    zoom: 10,
    viewMode: '2D',
    pitch: 0,
    resizeEnable: true
  })
}

const drawTrackOnMap = async () => {
  await nextTick()
  if (!orderDetail.value) return
  if (!map) {
    map = await initMap()
    if (!map) return
  } else {
    map.clearMap()
  }

  mapErrorMessage.value = '' // Reset error message

  const { status, trackList } = orderDetail.value
  // 只统计有经纬度的有效轨迹点
  const validTracks = trackList.filter(t => t.lng && t.lat)
  const path = validTracks.map(t => [t.lng, t.lat])

  // --- 降级处理 ---
  if (validTracks.length === 0) {
    mapErrorMessage.value = '无法获取订单地址的有效坐标，请检查寄件与收件地址是否正确。'
    map.setZoomAndCenter(4, [108.946609, 34.262324]); // Center on China
    console.warn("此订单无有效轨迹点可供显示。")
    return;
  }

  if (validTracks.length === 1) {
    const point = path[0]
    map.add(new AMap.Marker({ 
        position: point, 
        icon: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/start.png', 
        anchor: 'bottom-center'
    }))
    map.setCenter(point)
    map.setZoom(15)
    mapErrorMessage.value = '仅有一个地址坐标有效，无法规划完整轨迹。'
    return
  }

  // --- 正常绘制 ---
  if (path.length >= 2) {
    if (status === '未揽收') {
      // 下单未揽收，画红色虚线
      const polyline = new AMap.Polyline({
        path,
        borderWeight: 4,
        strokeColor: '#f5222d',
        strokeWeight: 6,
        lineJoin: 'round',
        strokeStyle: 'dashed',
        isOutline: true,
        outlineColor: '#fff'
      })
      map.add(polyline)
      map.add(new AMap.Marker({ position: path[0], icon: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/start.png', anchor: 'bottom-center' }))
      map.add(new AMap.Marker({ position: path[1], icon: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/end.png', anchor: 'bottom-center' }))
      
      const carMarker = new AMap.Marker({ 
        position: path[0], 
        icon: 'https://a.amap.com/jsapi_demos/static/demo-center-v2/car.png', 
        offset: new AMap.Pixel(-13, -26), 
        anchor: 'bottom-center' 
      })
      carMarker.setLabel({ 
        direction: 'top', 
        content: `<div style="background:rgba(255,255,255,0.9); border:1px solid #ccc; padding: 4px 8px; border-radius:4px; font-size:12px; white-space:nowrap;">待揽收</div>` 
      })
      map.add(carMarker)

      map.setFitView()
      return
    }

    // 已揽收、运输中、已签收
    const polyline = new AMap.Polyline({
      path,
      borderWeight: 2,
      strokeColor: "#28F",
      strokeWeight: 6,
      lineJoin: 'round',
    })
    map.add(polyline)
    map.add(new AMap.Marker({ position: path[0], icon: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/start.png', anchor: 'bottom-center' }))
    map.add(new AMap.Marker({ position: path[path.length - 1], icon: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/end.png', anchor: 'bottom-center' }))
    if (path.length > 2) {
      // 途经点
      const stationMarker = new AMap.Marker({ position: path[1], icon: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/dir-via-marker.png', anchor: 'bottom-center' })
      map.add(stationMarker)
    }
    
    let carPosition, carLabel
    if (status === '已揽收') {
      carPosition = path[0]; carLabel = '快递员已揽收'
    } else if (status === '运输中' && path.length > 2) {
      carPosition = path[1]; carLabel = '运输中'
    } else if (status === '已签收') {
      carPosition = path[path.length - 1]; carLabel = '已签收'
    }

    if (carPosition) {
      const carMarker = new AMap.Marker({ position: carPosition, icon: 'https://a.amap.com/jsapi_demos/static/demo-center-v2/car.png', offset: new AMap.Pixel(-13, -26), anchor: 'bottom-center' })
      carMarker.setLabel({ direction: 'top', content: `<div style=\"background:rgba(255,255,255,0.9); border:1px solid #ccc; padding: 4px 8px; border-radius:4px; font-size:12px; white-space:nowrap;\">${carLabel}</div>` })
      map.add(carMarker)
    }
    
    map.setFitView()
  }
}

// --- 组件生命周期与业务逻辑 ---
onMounted(async () => {
  orderNoInput.value = route.query.orderNo || ''
  if (orderNoInput.value) {
    await loadOrderTrack()
  }
})

const loadOrderTrack = async () => {
  if (!orderNoInput.value) {
    alert('请输入订单号')
    return
  }
  searched.value = true
  orderDetail.value = null
  if (map) { map.destroy(); map = null }
  try {
    const res = await axios.get(`/api/user/order/detail?orderNo=${orderNoInput.value}`)
    orderDetail.value = res.data
    await drawTrackOnMap()
  } catch (e) {
    console.error('获取订单详情失败：', e)
  }
}

// --- 辅助函数 ---
const getStatusClass = (status) => {
  switch (status) {
    case '已揽收': return 'status-received'
    case '运输中': return 'status-transit'
    case '已签收': return 'status-delivered'
    default: return 'status-default'
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}
</script>

<style scoped>
h2 {
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 20px;
  font-weight: 500;
}
.user-track {
  width: 100%;
}
.track-controls {
  margin-bottom: 24px;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}
.form-group {
  display: flex;
  align-items: center;
  gap: 12px;
}
.form-group label {
  font-weight: 500;
  color: #555;
}
.form-group input {
  flex-grow: 1;
  padding: 10px 14px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}
.form-group button {
  background: #409eff;
  color: #fff;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
}
.track-info {
  margin-bottom: 24px;
}
.order-summary {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}
.order-summary h3 {
  margin-top: 0;
  margin-bottom: 16px;
  font-weight: 500;
}
.summary-item {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;
}
.summary-item label {
  width: 100px;
  font-weight: 500;
  color: #666;
}
.status {
  padding: 5px 10px;
  border-radius: 12px;
  font-size: 12px;
  color: #fff;
  font-weight: 500;
}
.status-received { background: #409eff; }
.status-transit { background: #e6a23c; }
.status-delivered { background: #67c23a; }
.status-default { background: #909399; }

.map-container {
  position: relative;
  height: 400px;
  background: #f7f9fc;
  border-radius: 8px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  overflow: hidden;
}
.map-error-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.05);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
  pointer-events: none; /* Allows map interaction */
}
.map-error-overlay p {
  background: rgba(255, 255, 255, 0.95);
  padding: 15px 25px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  color: #f56c6c;
  font-weight: 500;
}
.track-timeline {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}
.track-timeline h3 {
  margin-top: 0;
  margin-bottom: 20px;
  font-weight: 500;
}
.timeline {
  position: relative;
  padding-left: 25px;
  border-left: 2px solid #eef0f2;
}
.timeline-item {
  position: relative;
  margin-bottom: 25px;
}
.timeline-item:last-child {
  margin-bottom: 0;
}
.timeline-dot {
  position: absolute;
  left: -35px;
  top: 5px;
  width: 14px;
  height: 14px;
  background: #409eff;
  border-radius: 50%;
  border: 3px solid #fff;
  box-shadow: 0 0 0 3px #409eff;
}
.timeline-content {
  background: #fafafa;
  padding: 16px;
  border-radius: 6px;
  margin-left: 10px;
}
.timeline-time {
  font-size: 13px;
  color: #666;
  margin-bottom: 6px;
}
.timeline-location {
  font-weight: 500;
  color: #333;
  margin-bottom: 6px;
}
.timeline-status {
  color: #409eff;
  font-size: 14px;
  margin-bottom: 4px;
}
.timeline-operator {
  color: #888;
  font-size: 12px;
}
.no-selection {
  text-align: center;
  margin-top: 40px;
  color: #888;
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}
</style> 