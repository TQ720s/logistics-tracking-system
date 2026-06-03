// 清空旧数据
db.users.deleteMany({});
db.couriers.deleteMany({});
db.admins.deleteMany({});
db.orders.deleteMany({});

// 初始化用户
var userId1 = ObjectId();
db.users.insertOne({
  _id: userId1,
  username: "user1",
  password: "123456",
  name: "张三",
  phone: "13800000001",
  addressList: [
    { id: ObjectId(), name: "张三", phone: "13800000001", address: "北京市朝阳区三里屯" },
    { id: ObjectId(), name: "李四", phone: "13800000002", address: "上海市浦东新区陆家嘴" }
  ],
  email: "2796117468@qq.com"
});

// 初始化快递员
var courierId1 = ObjectId();
db.couriers.insertOne({
  _id: courierId1,
  username: "courier1",
  password: "123456",
  name: "王师傅",
  phone: "13900000001",
  email: "courier1@test.com"
});

// 初始化管理员
db.admins.insertOne({
  username: "admin",
  password: "123456",
  name: "管理员",
  phone: "13700000001",
  email: "admin@test.com"
});

// 初始化订单
var now = new Date();
var beijing = { lng: 116.472802, lat: 39.922985 }; // 北京市朝阳区三里屯
var shanghai = { lng: 121.509742, lat: 31.242337 }; // 上海市浦东新区陆家嘴
var guangzhou = { lng: 113.327172, lat: 23.132223 }; // 广州市天河区
var wuhan = { lng: 114.305215, lat: 30.592761 }; // 武汉市
var changsha = { lng: 112.982278, lat: 28.19409 };  // 长沙市

db.orders.insertMany([
  {
    orderNo: "OD10001",
    userId: userId1,
    sender: { name: "张三", phone: "13800000001", address: "北京市朝阳区三里屯" },
    receiver: { name: "李四", phone: "13800000002", address: "上海市浦东新区陆家嘴" },
    weight: 2.5,
    fee: 15.5,
    status: "已揽收",
    courierUsername: "courier1",
    createTime: now,
    trackList: [
      { time: now, location: "北京市朝阳区三里屯", lng: beijing.lng, lat: beijing.lat, operator: "courier1", status: "快递员已揽收" }
    ]
  },
  {
    orderNo: "OD10002",
    userId: userId1,
    sender: { name: "张三", phone: "13800000001", address: "广州市天河区" },
    receiver: { name: "李四", phone: "13800000002", address: "上海市浦东新区陆家嘴" },
    weight: 1.2,
    fee: 12.6,
    status: "运输中",
    courierUsername: "courier1",
    createTime: new Date(now.getTime() - 86400000), // 1天前
    trackList: [
      { time: new Date(now.getTime() - 86400000), location: "广州市天河区", lng: guangzhou.lng, lat: guangzhou.lat, operator: "courier1", status: "快递员已揽收" },
      { time: now, location: "长沙市", lng: changsha.lng, lat: changsha.lat, operator: "courier1", status: "运输中" }
    ]
  },
  {
    orderNo: "OD10003",
    userId: userId1,
    sender: { name: "张三", phone: "13800000001", address: "北京市朝阳区三里屯" },
    receiver: { name: "王五", phone: "13800000003", address: "广州市天河区" },
    weight: 0.8,
    fee: 12.0,
    status: "已签收",
    courierUsername: "courier1",
    createTime: new Date(now.getTime() - 172800000), // 2天前
    trackList: [
      { time: new Date(now.getTime() - 172800000), location: "北京市朝阳区三里屯", lng: beijing.lng, lat: beijing.lat, operator: "courier1", status: "快递员已揽收" },
      { time: new Date(now.getTime() - 86400000), location: "武汉市", lng: wuhan.lng, lat: wuhan.lat, operator: "courier1", status: "运输中" },
      { time: now, location: "广州市天河区", lng: guangzhou.lng, lat: guangzhou.lat, operator: "courier1", status: "已签收" }
    ]
  },
  {
    orderNo: "OD10004",
    userId: userId1,
    sender: { name: "张三", phone: "13800000001", address: "北京市朝阳区三里屯" },
    receiver: { name: "李四", phone: "13800000002", address: "上海市浦东新区陆家嘴" },
    weight: 1.0,
    fee: 12.0,
    status: "未揽收",
    courierId: null,
    createTime: now,
    trackList: [
      { time: now, location: "北京市朝阳区三里屯", lng: beijing.lng, lat: beijing.lat, operator: "user1", status: "未揽收" },
      { time: now, location: "上海市浦东新区陆家嘴", lng: shanghai.lng, lat: shanghai.lat, operator: null, status: "待配送" }
    ]
  }
]);

print('数据初始化完成！');
print('User: user1, Password: 123456');
print('Courier: courier1, Password: 123456');
print('Admin: admin, Password: 123456');

// 省会城市站点

db.stations.deleteMany({});
db.stations.insertMany([
  { name: "北京", lng: 116.405285, lat: 39.904989 },
  { name: "天津", lng: 117.190182, lat: 39.125596 },
  { name: "石家庄", lng: 114.502461, lat: 38.045474 },
  { name: "太原", lng: 112.549248, lat: 37.857014 },
  { name: "呼和浩特", lng: 111.670801, lat: 40.818311 },
  { name: "沈阳", lng: 123.429096, lat: 41.796767 },
  { name: "长春", lng: 125.3245, lat: 43.886841 },
  { name: "哈尔滨", lng: 126.642464, lat: 45.756967 },
  { name: "上海", lng: 121.472644, lat: 31.231706 },
  { name: "南京", lng: 118.767413, lat: 32.041544 },
  { name: "杭州", lng: 120.153576, lat: 30.287459 },
  { name: "合肥", lng: 117.283042, lat: 31.86119 },
  { name: "福州", lng: 119.306239, lat: 26.075302 },
  { name: "南昌", lng: 115.892151, lat: 28.676493 },
  { name: "济南", lng: 117.000923, lat: 36.675807 },
  { name: "郑州", lng: 113.665412, lat: 34.757975 },
  { name: "武汉", lng: 114.298572, lat: 30.584355 },
  { name: "长沙", lng: 112.982279, lat: 28.19409 },
  { name: "广州", lng: 113.280637, lat: 23.125178 },
  { name: "南宁", lng: 108.320004, lat: 22.82402 },
  { name: "海口", lng: 110.33119, lat: 20.031971 },
  { name: "重庆", lng: 106.504962, lat: 29.533155 },
  { name: "成都", lng: 104.065735, lat: 30.659462 },
  { name: "贵阳", lng: 106.713478, lat: 26.578343 },
  { name: "昆明", lng: 102.712251, lat: 25.040609 },
  { name: "拉萨", lng: 91.132212, lat: 29.660361 },
  { name: "西安", lng: 108.948024, lat: 34.263161 },
  { name: "兰州", lng: 103.823557, lat: 36.058039 },
  { name: "西宁", lng: 101.778916, lat: 36.623178 },
  { name: "银川", lng: 106.278179, lat: 38.46637 },
  { name: "乌鲁木齐", lng: 87.617733, lat: 43.792818 },
  { name: "台北", lng: 121.509062, lat: 25.044332 },
  { name: "香港", lng: 114.173355, lat: 22.320048 },
  { name: "澳门", lng: 113.54909, lat: 22.198951 }
]);

// 初始化命令:
// mongosh
// use logistics_demo
// load("D:/SprigBoot/logistics_demo/src/main/resources/db/initData.js")
// (请将上面的路径替换为您本地的绝对路径)