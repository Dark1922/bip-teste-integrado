#!/bin/bash

echo " Iniciando Backend..."

cd backend-module

# Compilar apenas o código principal, sem testes
echo "📦 Compilando código principal..."
mvn clean compile -DskipTests

if [ $? -eq 0 ]; then
    echo " Compilação bem-sucedida!"
    
    # Executar o backend
    echo "🏃 Executando backend..."
    mvn spring-boot:run -DskipTests &
    
    # Aguardar inicialização
    echo "⏳ Aguardando backend inicializar..."
    sleep 15
    
    # Verificar se está rodando
    if curl -s http://localhost:8080/api/v1/beneficios > /dev/null; then
        echo " Backend iniciado com sucesso!"
        echo " API disponível em: http://localhost:8080/api/v1/beneficios"
        echo " Swagger disponível em: http://localhost:8080/swagger-ui.html"
    else
        echo " Backend não conseguiu iniciar. Verifique os logs."
    fi
else
    echo " Falha na compilação do backend"
    exit 1
fi
