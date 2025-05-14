// Arquivo: ./mongodb-init/create-indexes.js
db = db.getSiblingDB("chatbot");

// Criar a collection se não existir
if (!db.getCollectionNames().includes('embeddings')) {
  db.createCollection("embeddings");
  print("Collection 'embeddings' criada com sucesso.");
}

// Criar índice vetorial para pesquisa semântica
db.embeddings.createIndex(
  { embedding: "vector" },
  {
    name: "test",
    vectorOptions: {
      dimensions: 1536,  // ajuste conforme o tamanho dos seus vetores
      similarity: "cosine"  // opções: cosine, euclidean, dotProduct
    }
  }
);

print("Índice vetorial criado com sucesso na collection 'embeddings'.");