<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>主页</title>
  </head>
  <body>
  这里是主页
  <br/>
这里展示表单
  <form action="${pageContext.request.contextPath}/wx" method="get">
    <p>signature: <input type="text" name="signature" /></p>
    <p>timestamp: <input type="text" name="timestamp" /></p>
    <p>nonce: <input type="text" name="nonce" /></p>
    <p>echostr: <input type="text" name="echostr" /></p>

    <input type="submit" value="提交" />
  </form>
  </body>
</html>
