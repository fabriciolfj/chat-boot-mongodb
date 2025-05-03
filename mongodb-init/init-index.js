db = db.getSiblingDB("chatbot");

db.createCollection("embeddings");

db.embeddings.createIndex(
  { embedding: "cosmos-vector" },
  {
    name: "test",
    type: "vectorSearch",
    options: {
      dimensions: 1536,  // ajuste conforme o tamanho dos seus vetores
      similarity: "cosine"
    }
  }
);
