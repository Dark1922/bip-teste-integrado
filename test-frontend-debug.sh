#!/bin/bash

echo " Teste Específico - Debug Frontend"
echo "===================================="

# Dados que o frontend está enviando
FRONTEND_DATA='{"nome":"teste","descricao":"teste","valor":100,"ativo":true}'

echo "1. Testando com dados exatos do frontend..."
echo "Dados: $FRONTEND_DATA"

# Teste 1: POST direto
echo ""
echo "Teste 1: POST direto"
RESPONSE1=$(curl -s -X POST http://localhost:8080/api/v1/beneficios \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:49737" \
  -d "$FRONTEND_DATA")

echo "Resposta: $RESPONSE1"

if echo "$RESPONSE1" | grep -q "id"; then
    echo " POST funcionando"
else
    echo " POST falhando"
    echo "Erro: $RESPONSE1"
fi

# Teste 2: Verificar se dados foram salvos
echo ""
echo "Teste 2: Verificando dados salvos"
LIST_RESPONSE=$(curl -s http://localhost:8080/api/v1/beneficios)
echo "Lista atual: $LIST_RESPONSE"

# Teste 3: Testar com dados diferentes
echo ""
echo "Teste 3: Testando com dados diferentes"
TEST_DATA='{"nome":"Teste Debug","descricao":"Descrição Debug","valor":200.00,"ativo":true}'

RESPONSE2=$(curl -s -X POST http://localhost:8080/api/v1/beneficios \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:49737" \
  -d "$TEST_DATA")

echo "Resposta: $RESPONSE2"

# Teste 4: Verificar logs do backend
echo ""
echo "Teste 4: Verificando se há logs de erro"
echo "Verifique os logs do backend para erros..."

# Teste 5: Testar validações
echo ""
echo "Teste 5: Testando validações"
INVALID_DATA='{"nome":"","valor":-100}'

RESPONSE3=$(curl -s -X POST http://localhost:8080/api/v1/beneficios \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:49737" \
  -d "$INVALID_DATA")

echo "Dados inválidos: $INVALID_DATA"
echo "Resposta: $RESPONSE3"

if echo "$RESPONSE3" | grep -q "400\|error\|Bad Request"; then
    echo " Validações funcionando"
else
    echo "  Validações podem não estar funcionando"
fi

echo ""
echo " Próximos passos:"
echo "1. Se todos os testes passaram, o problema está no frontend"
echo "2. Abra DevTools no navegador (F12)"
echo "3. Vá para aba Console"
echo "4. Tente criar um benefício"
echo "5. Verifique se há erros JavaScript"
echo "6. Vá para aba Network para ver as requisições HTTP"
echo ""
echo " Se o problema persistir, verifique:"
echo "- Se o Angular está fazendo a requisição corretamente"
echo "- Se há erros de JavaScript no console"
echo "- Se a requisição está sendo enviada para a URL correta"
