<#-- @ftlvariable name="" type="net.binggl.mydms.application.Mydms403View" -->
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="Central authentication for all my applications.">
    <meta name="author" content="Henrik Binggl">
    <link rel="shortcut icon" href="/static/favicon.ico">
    <title>mydms.binggl.net : Authentication Error</title>

    <link href="/static/css/bootstrap.min.css" rel="stylesheet">
    <link href="/static/css/font-awesome.min.css" rel="stylesheet">
    <link href="/static/css/mydms.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
  <body>

    
    <div class="container">
    
    
        <div class="row" style="padding-bottom: 40px; text-align:center;">
          <div class="col-md-3"></div>
          <div class="col-md-6">
            <h1>Login with Google Account</h1>
            
          </div>
          <div class="col-md-3"></div>
        </div>
        
        <div class="row">
          <div class="col-md-3"></div>
          <div class="col-md-6" style="text-align:center;">
            <img src="/static/images/Google_2015_logo.svg" style="width:200px"/>
          </div>
          <div class="col-md-3"></div>
        </div>
        
        <div class="row">
          <div class="col-md-3"></div>
          <div class="col-md-6">
            <blockquote>
            <p>The authentication is done via Google, the authorization is done via login.binggl.net. Once you click on the login button you are forwarded to a authentication sytem. After the process is finished you are redirected to this application.</p>
            </blockquote>
          </div>
          <div class="col-md-3"></div>
        </div>
        
        
        
        <div class="row">
          <div class="col-md-4"></div>
          <div class="col-md-4">
          
            <form class="form-signin">
                <a href="https://login.binggl.net/auth/flow?~site=mydms&~url=https://mydms.binggl.net/" class="btn btn-lg btn-success btn-block" type="submit"><i class="fa fa-google"></i>: Go to login.binggl.net!</a>
            </form>
            
          </div>
          <div class="col-md-4"></div>
        </div>
    
    </div> <!-- /container -->
    
    
    <footer class="footer hidden-md hidden-xs">
      <div class="container">
       <p class="text-muted"> &copy; ${year} Henrik Binggl | <i class="fa fa-lock"></i> ${appName} application</p>
      </div>
    </footer>
    
   

    <script src="https://code.jquery.com/jquery-2.1.4.min.js"></script>
    <script src="/static/js/bootstrap.min.js"></script>
  </body>
</html>

