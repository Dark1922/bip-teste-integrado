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
