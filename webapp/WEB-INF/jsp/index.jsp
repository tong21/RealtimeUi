<%--
  Created by IntelliJ IDEA.
  User: tong
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html style="height: 100%">
<head>
    <meta charset="utf-8">
    <title>商品销售额实时展示</title>
</head>
<body style="height: 100%; margin: 0">
<div id="container" style="height: 30%;width: 30%"></div>
<div id="message"></div>
<script src="/js/jquery-1.8.3.min.js"></script>
<script src="/js/echarts.min.js"></script>
<script type="text/javascript">
    var myChart = echarts.init(document.getElementById('container'));
    myChart.setOption({
        title: {
            text: '商品销售额汇总',
            subtext: '数据来自模拟'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['2018 年 11 月 11 日']
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'value',
            boundaryGap: [0, 0.01]
        },
        yAxis: {
            type: 'category',
            data : []
        },
        series: [
            {
                name: '2018 年 11 月 11 日',
                type: 'bar',
                data : []
            }
        ]
    });
    //隐藏加载动画
    myChart.hideLoading();
    var websocket = null;
    //判断当前浏览器是否支持 WebSocket
    if ('WebSocket' in window) {
        websocket = new WebSocket("ws://localhost:8080/uiwebSocket");
    }
    else {
        alert('当前浏览器 Not support websocket')
    }
    //连接发生错误的回调方法
    websocket.onerror = function () {
        setMessageInnerHTML("WebSocket 连接发生错误");
    };
    //连接成功建立的回调方法
    websocket.onopen = function () {
        setMessageInnerHTML("WebSocket 连接成功");
    }
    //接收到消息的回调方法
    websocket.onmessage = function (event) {
        jsonbean = JSON.parse(event.data);
        //alert(jsonbean);
        //填充数据
        myChart.setOption({
            yAxis : {
                data : jsonbean.produceId
            },
            series : [{
                // 根据名字对应到相应的系列
                data : jsonbean.producetSumPrice
            }]
        })
        setMessageInnerHTML(event.data);
    }
    //连接关闭的回调方法
    websocket.onclose = function () {
        setMessageInnerHTML("WebSocket 连接关闭");
    }
    //监听窗口关闭事件，当窗口关闭时，主动去关闭 websocket 连接，防止连接还没断开就关闭窗口，server 端会抛异常。
 window.onbeforeunload = function () {
     closeWebSocket();
 }
    //将消息显示在网页上
    function setMessageInnerHTML(innerHTML) {
        //document.getElementById('message').innerHTML += innerHTML + '<br/>';
    }
    //关闭 WebSocket 连接
    function closeWebSocket() {
        websocket.close();
    }
</script>
</body>
</html>