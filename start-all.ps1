Write-Host "=============================================" -ForegroundColor Cyan
Write-Host "   CARTIFY MICROSERVICES SYSTEM STARTUP" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

# 1. Start Eureka Server
Write-Host "1. Starting Eureka Discovery Service [Port 8761]..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Title 'Eureka Server'; java -jar discovery-service/target/discovery-service-1.0.0.jar" -WindowStyle Normal

Write-Host "Waiting 12 seconds for Eureka registry server to initialize..." -ForegroundColor Yellow
Start-Sleep -Seconds 12

# 2. Start Downstream Microservices
Write-Host "2. Starting Identity & User Service [Port 8081]..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Title 'User Service (Auth)'; java -jar user-service/target/user-service-1.0.0.jar" -WindowStyle Normal

Write-Host "3. Starting Product Catalog & Inventory Service [Port 8082]..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Title 'Product Service'; java -jar product-service/target/product-service-1.0.0.jar" -WindowStyle Normal

Write-Host "4. Starting Order Checkout & Shopping Cart Service [Port 8083]..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Title 'Order Service'; java -jar order-service/target/order-service-1.0.0.jar" -WindowStyle Normal

Write-Host "Waiting 8 seconds for microservices to register with Eureka..." -ForegroundColor Yellow
Start-Sleep -Seconds 8

# 3. Start API Gateway
Write-Host "5. Starting Cloud API Gateway Routing [Port 8080]..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Title 'API Gateway'; java -jar gateway-service/target/gateway-service-1.0.0.jar" -WindowStyle Normal

# 4. Start React Frontend
Write-Host "6. Starting React Frontend Development Server..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Title 'React Frontend'; cd frontend; npm run dev" -WindowStyle Normal

Write-Host "---------------------------------------------" -ForegroundColor Cyan
Write-Host "System initialized!" -ForegroundColor Cyan
Write-Host "API Gateway URL: http://localhost:8080" -ForegroundColor White
Write-Host "React Frontend URL: http://localhost:5173" -ForegroundColor White
Write-Host "Eureka Dashboard: http://localhost:8761" -ForegroundColor White
Write-Host "=============================================" -ForegroundColor Cyan
