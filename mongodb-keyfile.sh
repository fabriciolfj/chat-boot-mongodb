#!/bin/bash

# Cria um arquivo de chave seguro para autenticação do MongoDB replicaset
echo "Criando arquivo de chave para MongoDB..."
openssl rand -base64 756 > mongo-keyfile1
chmod 400 mongo-keyfile1

# Garantir que o Docker possa ler o arquivo
echo "Ajustando permissões..."
sudo chown 999:999 mongo-keyfile1

echo "Arquivo de chave criado com sucesso: mongo-keyfile"
echo "Agora você pode executar o docker-compose."