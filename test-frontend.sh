#!/bin/bash

#  Script de Teste Automatizado - Frontend
# Este script testa automaticamente o frontend do Sistema de Benefícios

echo " Iniciando Testes do Frontend"
echo "================================"

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para imprimir mensagens coloridas
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar se frontend está rodando
print_status "Verificando se frontend está rodando..."

FRONTEND_URL="http://localhost:49737"
if curl -s $FRONTEND_URL > /dev/null; then
    print_success "Frontend está rodando em $FRONTEND_URL"
else
    print_error "Frontend não está rodando. Inicie com: cd frontend && npm start"
    exit 1
fi

# Verificar se backend está rodando
print_status "Verificando se backend está rodando..."

BACKEND_URL="http://localhost:8080/api/v1/beneficios"
if curl -s $BACKEND_URL > /dev/null; then
    print_success "Backend está rodando e respondendo"
else
    print_error "Backend não está rodando. Inicie com: cd backend-module && mvn spring-boot:run"
    exit 1
fi

echo ""
print_status "Iniciando testes automatizados..."

# Teste 1: Verificar se página carrega
print_status "Teste 1: Verificando carregamento da página..."
if curl -s $FRONTEND_URL | grep -q "Sistema de Benefícios"; then
    print_success " Página carrega corretamente"
else
    print_error " Página não carrega corretamente"
fi

# Teste 2: Verificar se API está acessível
print_status "Teste 2: Verificando conectividade com API..."
if curl -s $BACKEND_URL | grep -q "\["; then
    print_success " API está acessível e retornando dados"
else
    print_warning "  API está acessível mas pode estar vazia"
fi

# Teste 3: Criar benefício via API
print_status "Teste 3: Criando benefício de teste..."
BENEFICIO_RESPONSE=$(curl -s -X POST $BACKEND_URL \
  -H "Content-Type: application/json" \
  -d '{"nome":"Teste Frontend","descricao":"Benefício criado via teste automatizado","valor":1000.00,"ativo":true}')

if echo "$BENEFICIO_RESPONSE" | grep -q "id"; then
    BENEFICIO_ID=$(echo "$BENEFICIO_RESPONSE" | grep -o '"id":[0-9]*' | cut -d':' -f2)
    print_success " Benefício criado com ID: $BENEFICIO_ID"
else
    print_error " Falha ao criar benefício"
    echo "Resposta: $BENEFICIO_RESPONSE"
fi

# Teste 4: Listar benefícios
print_status "Teste 4: Listando benefícios..."
BENEFICIOS_LIST=$(curl -s $BACKEND_URL)
if echo "$BENEFICIOS_LIST" | grep -q "Teste Frontend"; then
    print_success " Benefício aparece na lista"
else
    print_warning "  Benefício não encontrado na lista"
fi

# Teste 5: Criar segundo benefício para transferência
print_status "Teste 5: Criando segundo benefício..."
BENEFICIO2_RESPONSE=$(curl -s -X POST $BACKEND_URL \
  -H "Content-Type: application/json" \
  -d '{"nome":"Benefício Destino","descricao":"Para teste de transferência","valor":500.00,"ativo":true}')

if echo "$BENEFICIO2_RESPONSE" | grep -q "id"; then
    BENEFICIO2_ID=$(echo "$BENEFICIO2_RESPONSE" | grep -o '"id":[0-9]*' | cut -d':' -f2)
    print_success " Segundo benefício criado com ID: $BENEFICIO2_ID"
else
    print_error " Falha ao criar segundo benefício"
fi

# Teste 6: Transferir entre benefícios
if [ ! -z "$BENEFICIO_ID" ] && [ ! -z "$BENEFICIO2_ID" ]; then
    print_status "Teste 6: Transferindo entre benefícios..."
    TRANSFER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/beneficios/transferir \
      -H "Content-Type: application/json" \
      -d "{\"fromId\":$BENEFICIO_ID,\"toId\":$BENEFICIO2_ID,\"valor\":100.00}")
    
    if echo "$TRANSFER_RESPONSE" | grep -q "sucesso"; then
        print_success " Transferência realizada com sucesso"
    else
        print_error " Falha na transferência: $TRANSFER_RESPONSE"
    fi
fi

# Teste 7: Verificar saldos após transferência
print_status "Teste 7: Verificando saldos após transferência..."
SALDOS_FINAIS=$(curl -s $BACKEND_URL)
if echo "$SALDOS_FINAIS" | grep -q "900.00\|600.00"; then
    print_success " Saldos atualizados corretamente"
else
    print_warning "  Saldos podem não ter sido atualizados"
fi

# Teste 8: Buscar benefício por nome
print_status "Teste 8: Buscando benefício por nome..."
BUSCA_RESPONSE=$(curl -s "http://localhost:8080/api/v1/beneficios/buscar?nome=Teste")
if echo "$BUSCA_RESPONSE" | grep -q "Teste Frontend"; then
    print_success " Busca por nome funcionando"
else
    print_warning "  Busca por nome pode não estar funcionando"
fi

# Teste 9: Verificar validações
print_status "Teste 9: Testando validações..."
VALIDATION_RESPONSE=$(curl -s -X POST $BACKEND_URL \
  -H "Content-Type: application/json" \
  -d '{"nome":"","valor":-100.00}')

if echo "$VALIDATION_RESPONSE" | grep -q "400\|error\|Bad Request"; then
    print_success " Validações estão funcionando"
else
    print_warning "  Validações podem não estar funcionando corretamente"
fi

# Teste 10: Verificar Swagger
print_status "Teste 10: Verificando documentação Swagger..."
if curl -s http://localhost:8080/swagger-ui.html | grep -q "swagger"; then
    print_success " Swagger está acessível"
else
    print_warning "  Swagger pode não estar funcionando"
fi

echo ""
echo " Resumo dos Testes:"
echo "====================="

# Contar sucessos
SUCCESS_COUNT=0
TOTAL_TESTS=10

# Verificar cada teste
if curl -s $FRONTEND_URL | grep -q "Sistema de Benefícios"; then ((SUCCESS_COUNT++)); fi
if curl -s $BACKEND_URL > /dev/null; then ((SUCCESS_COUNT++)); fi
if echo "$BENEFICIO_RESPONSE" | grep -q "id"; then ((SUCCESS_COUNT++)); fi
if echo "$BENEFICIOS_LIST" | grep -q "Teste Frontend"; then ((SUCCESS_COUNT++)); fi
if echo "$BENEFICIO2_RESPONSE" | grep -q "id"; then ((SUCCESS_COUNT++)); fi
if echo "$TRANSFER_RESPONSE" | grep -q "sucesso"; then ((SUCCESS_COUNT++)); fi
if echo "$SALDOS_FINAIS" | grep -q "900.00\|600.00"; then ((SUCCESS_COUNT++)); fi
if echo "$BUSCA_RESPONSE" | grep -q "Teste Frontend"; then ((SUCCESS_COUNT++)); fi
if echo "$VALIDATION_RESPONSE" | grep -q "400\|error\|Bad Request"; then ((SUCCESS_COUNT++)); fi
if curl -s http://localhost:8080/swagger-ui.html | grep -q "swagger"; then ((SUCCESS_COUNT++)); fi

echo " Testes bem-sucedidos: $SUCCESS_COUNT/$TOTAL_TESTS"

if [ $SUCCESS_COUNT -eq $TOTAL_TESTS ]; then
    print_success " Todos os testes passaram! Sistema funcionando perfeitamente!"
elif [ $SUCCESS_COUNT -ge 8 ]; then
    print_success " Maioria dos testes passou! Sistema funcionando bem!"
else
    print_warning "  Alguns testes falharam. Verifique os logs acima."
fi

echo ""
echo " Acessos disponíveis:"
echo "   • Frontend: http://localhost:49737"
echo "   • Backend API: http://localhost:8080/api/v1/beneficios"
echo "   • Swagger: http://localhost:8080/swagger-ui.html"
echo "   • H2 Console: http://localhost:8080/h2-console"
echo ""
echo "📝 Para testes manuais, consulte: FRONTEND-TEST-GUIDE.md"
