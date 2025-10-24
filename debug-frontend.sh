#!/bin/bash

echo " Debug Frontend - Sistema de Benefícios"
echo "========================================="

# Verificar se frontend está rodando
echo "1. Verificando frontend..."
if curl -s http://localhost:49737 > /dev/null; then
    echo " Frontend rodando em http://localhost:49737"
else
    echo " Frontend não está rodando"
    exit 1
fi

# Verificar se backend está rodando
echo "2. Verificando backend..."
if curl -s http://localhost:8080/api/v1/beneficios > /dev/null; then
    echo " Backend rodando em http://localhost:8080"
else
    echo " Backend não está rodando"
    exit 1
fi

# Testar CORS
echo "3. Testando CORS..."
CORS_RESPONSE=$(curl -s -X OPTIONS http://localhost:8080/api/v1/beneficios \
  -H "Origin: http://localhost:49737" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -v 2>&1)

if echo "$CORS_RESPONSE" | grep -q "Access-Control-Allow-Origin"; then
    echo " CORS configurado corretamente"
else
    echo "  CORS pode não estar configurado"
fi

# Testar criação via API
echo "4. Testando criação via API..."
API_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/beneficios \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:49737" \
  -d '{"nome":"Debug Test","descricao":"Teste de debug","valor":100.00,"ativo":true}')

if echo "$API_RESPONSE" | grep -q "id"; then
    echo " API funcionando - Benefício criado"
    echo "Resposta: $API_RESPONSE"
else
    echo " API não está funcionando"
    echo "Resposta: $API_RESPONSE"
fi

# Verificar se há dados
echo "5. Verificando dados existentes..."
DADOS=$(curl -s http://localhost:8080/api/v1/beneficios)
echo "Dados atuais: $DADOS"

echo ""
echo " Próximos passos:"
echo "1. Abra o navegador em http://localhost:49737"
echo "2. Abra DevTools (F12)"
echo "3. Vá para aba Console"
echo "4. Tente criar um benefício"
echo "5. Verifique se há erros no console"
echo "6. Vá para aba Network para ver as requisições"
