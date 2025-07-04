# ⚡ Bitespeed Identity Reconciliation

A lightweight backend service to reconcile and unify customer identities based on overlapping email or phone number inputs.

---

## ✅ Hosted API

**POST**  
`https://bitespeed-identity-reconciliation-z4q7.onrender.com/identify`

> 🧊 **Heads up!** This is deployed on Render’s free tier. If it's been idle for a while, the server might be napping 💤 — first request can take a few seconds to respond (cold start). Hang in there!

---

## 🔁 API Usage

### 📬 Request Format

```json
{
  "email": "user@example.com",
  "phoneNumber": "1234567890"
}
```
---
## 🧱 Tech Stack  
- Java 17  
- Spring Boot 3  
- PostgreSQL  
- Maven  
- Render (free hosting)
