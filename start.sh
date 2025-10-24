#!/bin/bash

echo "Iniciando Sistema de Gestão de Benefícios"
echo "=========================================="

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

# Verificar pré-requisitos
print_status "Verificando pré-requisitos..."

# Java
if ! command -v java &> /dev/null; then
    print_error "Java não encontrado. Instale Java 17+ e tente novamente."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    print_error "Java 17+ é necessário. Versão atual: $JAVA_VERSION"
    exit 1
fi
print_success "Java $JAVA_VERSION encontrado"

# Maven
if ! command -v mvn &> /dev/null; then
    print_error "Maven não encontrado. Instale Maven 3.8+ e tente novamente."
    exit 1
fi
print_success "Maven encontrado"

# Node.js
if ! command -v node &> /dev/null; then
    print_error "Node.js não encontrado. Instale Node.js 18+ e tente novamente."
    exit 1
fi
print_success "Node.js encontrado"

# NPM
if ! command -v npm &> /dev/null; then
    print_error "NPM não encontrado. Instale NPM e tente novamente."
    exit 1
fi
print_success "NPM encontrado"

echo ""
print_status "Iniciando configuração do backend..."

# Backend - Instalar dependências e compilar
cd backend-module
print_status "Compilando backend..."
if mvn clean compile -q; then
    print_success "Backend compilado com sucesso"
else
    print_error "Falha na compilação do backend"
    exit 1
fi

# Executar testes
print_status "Executando testes do backend..."
if mvn test -q; then
    print_success "Testes do backend executados com sucesso"
else
    print_warning "Alguns testes falharam, mas continuando..."
fi

# Iniciar backend em background
print_status "Iniciando backend na porta 8080..."
mvn spring-boot:run > ../backend.log 2>&1 &
BACKEND_PID=$!
echo $BACKEND_PID > ../backend.pid

# Aguardar backend inicializar
print_status "Aguardando backend inicializar..."
sleep 10

# Verificar se backend está rodando
if curl -s http://localhost:8080/actuator/health > /dev/null; then
    print_success "Backend iniciado com sucesso!"
    print_status "API disponível em: http://localhost:8080/api/v1/beneficios"
    print_status "Swagger disponível em: http://localhost:8080/swagger-ui.html"
    print_status "H2 Console disponível em: http://localhost:8080/h2-console"
else
    print_warning "Backend pode não ter iniciado corretamente. Verifique backend.log"
fi

echo ""
print_status "Configurando frontend..."

# Frontend - Instalar dependências
cd ../frontend
print_status "Instalando dependências do frontend..."
if npm install --silent; then
    print_success "Dependências do frontend instaladas"
else
    print_error "Falha na instalação das dependências do frontend"
    exit 1
fi

# Iniciar frontend
print_status "Iniciando frontend na porta 4200..."
npm start > ../frontend.log 2>&1 &
FRONTEND_PID=$!
echo $FRONTEND_PID > ../frontend.pid

# Aguardar frontend inicializar
print_status "Aguardando frontend inicializar..."
sleep 15

# Verificar se frontend está rodando
if curl -s http://localhost:4200 > /dev/null; then
    print_success "Frontend iniciado com sucesso!"
    print_status "Aplicação disponível em: http://localhost:4200"
else
    print_warning "Frontend pode não ter iniciado corretamente. Verifique frontend.log"
fi

echo ""
echo " Sistema iniciado com sucesso!"
echo "================================"
echo ""
echo " Acessos disponíveis:"
echo "   • Frontend: http://localhost:4200"
echo "   • Backend API: http://localhost:8080/api/v1/beneficios"
echo "   • Swagger: http://localhost:8080/swagger-ui.html"
echo "   • H2 Console: http://localhost:8080/h2-console"
echo ""
echo "📝 Logs:"
echo "   • Backend: backend.log"
echo "   • Frontend: frontend.log"
echo ""
echo "🛑 Para parar o sistema, execute: ./stop.sh"
echo ""

# Criar script de parada
cat > ../stop.sh << 'EOF'
#!/bin/bash

echo "🛑 Parando Sistema de Benefícios..."

# Parar backend
if [ -f backend.pid ]; then
    BACKEND_PID=$(cat backend.pid)
    if kill -0 $BACKEND_PID 2>/dev/null; then
        kill $BACKEND_PID
        echo " Backend parado"
    fi
    rm -f backend.pid
fi

# Parar frontend
if [ -f frontend.pid ]; then
    FRONTEND_PID=$(cat frontend.pid)
    if kill -0 $FRONTEND_PID 2>/dev/null; then
        kill $FRONTEND_PID
        echo " Frontend parado"
    fi
    rm -f frontend.pid
fi

echo "🏁 Sistema parado com sucesso!"
EOF

chmod +x ../stop.sh

print_success "Script de parada criado: ./stop.sh"
