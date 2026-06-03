<template>
  <div class="courier-performance">
    <h2>绩效统计</h2>
    <div class="performance-card">
      <div class="performance-item">
        <label>已完成订单数：</label>
        <span class="count">{{ performance.completedOrders }}</span>
      </div>
      <div class="performance-item">
        <label>总奖金：</label>
        <span class="bonus">¥ {{ performance.bonus }}</span>
      </div>
    </div>
    
    <div class="chart-container">
      <h3>近7日完成订单量</h3>
      <div id="performance-chart" style="width: 100%; height: 400px;"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
// ECharts is not installed, so we will just show a placeholder
// import * as echarts from 'echarts';

const performance = ref({
  completedOrders: 0,
  bonus: 0
})

onMounted(async () => {
  const username = localStorage.getItem('username')
  if (username) {
    try {
      // Assuming a new backend endpoint for detailed performance
      // const res = await axios.get(`/api/courier/performance-details?username=${username}`);
      // For now, we use the existing endpoint and mock the rest
      const res = await axios.get(`/api/courier/performance?courierUsername=${username}`)
      performance.value.completedOrders = res.data
      performance.value.bonus = res.data * 1 // 1元 per order
    } catch (e) {
      console.error('获取绩效信息失败：', e)
    }
  }
  
  initChart()
})

const initChart = () => {
  const chartDom = document.getElementById('performance-chart');
  // Since echarts is not installed, show a placeholder
  if (chartDom) {
      chartDom.innerHTML = '<div style="height: 100%; background: #f9f9f9; display: flex; align-items: center; justify-content: center; color: #999; border-radius: 8px;">图表区域 - 需要安装ECharts来展示</div>'
      return
  }
  /*
  const myChart = echarts.init(chartDom);
  const option = {
    xAxis: {
      type: 'category',
      data: ['6天前', '5天前', '4天前', '昨天', '今天'] // Mock data
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: [5, 8, 3, 6, 10], // Mock data
        type: 'bar',
        showBackground: true,
        backgroundStyle: {
          color: 'rgba(180, 180, 180, 0.2)'
        }
      }
    ]
  };
  myChart.setOption(option);
  */
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
.performance-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 32px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  border: 1px solid #f0f0f0;
  display: flex;
  gap: 40px;
}
.performance-item {
  flex: 1;
}
.performance-item label {
  display: block;
  margin-bottom: 10px;
  font-size: 16px;
  color: #666;
}
.performance-item .count, .performance-item .bonus {
  font-size: 32px;
  font-weight: 600;
  color: #409eff;
}
.performance-item .bonus {
  color: #f56c6c;
}
.chart-container {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  border: 1px solid #f0f0f0;
}
.chart-container h3 {
  margin-top: 0;
  margin-bottom: 20px;
  font-weight: 500;
}
</style> 