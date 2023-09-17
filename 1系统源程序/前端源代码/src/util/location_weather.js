const key = "fc00df4960b42590a3484b4885d372c0"; //
async function getWeather(cityAdcode) {
  try {
    // 使用高德地图的天气API获取气温信息
    const weatherUrl = `https://restapi.amap.com/v3/weather/weatherInfo?key=${key}&city=${cityAdcode}`;
    const response = await fetch(weatherUrl);
    const weatherData = await response.json();

    // 提取气温信息
    if (weatherData.lives && weatherData.lives.length > 0) {
      const temperatureStr = weatherData.lives[0].temperature; // 当前气温字符串
      const weatherStr = weatherData.lives[0].weather; // 转换为数字
      console.log("天气:${temperatureStr}/${weatherStr}");
      return "天气" + temperatureStr + "°C" + "/" + weatherStr;
    } else {
      console.log(`未找到的天气信息`);
    }
  } catch (error) {
    console.error(`获取气温时发生错误：`, error);
  }
}
// 城市名称和adcode的数据
const cityData = [
  { name: "成都市", adcode: "510100" },
  { name: "甘孜藏族自治州", adcode: "513300" },
  { name: "宜宾市", adcode: "511500" },
  { name: "阿坝藏族羌族自治州", adcode: "513200" },
  { name: "广元市", adcode: "510800" },
  { name: "雅安市", adcode: "511800" },
  { name: "乐山市", adcode: "511100" },
  { name: "南充市", adcode: "511300" },
  { name: "绵阳市", adcode: "510700" },
  { name: "达州市", adcode: "511700" },
  { name: "眉山市", adcode: "511400" },
  { name: "泸州市", adcode: "510500" },
  { name: "巴中市", adcode: "511900" },
  { name: "广安市", adcode: "511600" },
  { name: "德阳市", adcode: "510600" },
  { name: "内江市", adcode: "511000" },
  { name: "攀枝花市", adcode: "510400" },
  { name: "自贡市", adcode: "510300" },
  { name: "遂宁市", adcode: "510900" },
  { name: "资阳市", adcode: "512000" },
];
// 城市温度数据对象
const cityTemperatures = {};
// 获取城市当前的气温
async function getCityWeather(cityName, cityAdcode) {
  try {
    // 使用高德地图的天气API获取气温信息
    const weatherUrl = `https://restapi.amap.com/v3/weather/weatherInfo?key=${key}&city=${cityAdcode}`;
    const response = await fetch(weatherUrl);
    const weatherData = await response.json();

    // 提取气温信息
    if (weatherData.lives && weatherData.lives.length > 0) {
      const temperatureStr = weatherData.lives[0].temperature; // 当前气温字符串
      const temperature = parseFloat(temperatureStr); // 转换为数字
      cityTemperatures[cityName] = temperature; // 存储在城市温度数据对象中
    } else {
      console.log(`未找到 ${cityName} 的天气信息`);
    }
  } catch (error) {
    console.error(`获取 ${cityName} 的气温时发生错误：`, error);
  }
}
// 获取所有城市的当前气温
async function getAllCityWeathers() {
  const promises = [];

  for (const cityInfo of cityData) {
    const promise = getCityWeather(cityInfo.name, cityInfo.adcode);
    promises.push(promise);
  }

  await Promise.all(promises);

  // 根据城市数据的顺序排序温度值
  const xData4Temperatures = cityData.map(
    (cityInfo) => cityTemperatures[cityInfo.name]
  );

  return xData4Temperatures; // 返回温度数组
}

export { getAllCityWeathers, getWeather };
