# Deployment Guide for Render.com

## Prerequisites
- MongoDB Atlas account with a database set up
- GitHub repository pushed with latest changes
- Render.com account (free tier)

## Steps to Deploy

### 1. Configure Environment Variables in Render

After creating your web service in Render, add these environment variables:

**Required:**
- `MONGODB_URI` = Your MongoDB Atlas connection string
  - Format: `mongodb+srv://<username>:<password>@<cluster>.mongodb.net/FLICKER?retryWrites=true&w=majority`
  - Get this from MongoDB Atlas → Database → Connect → Connect your application

**Optional:**
- `OPENAI_API_KEY` = Your OpenAI API key (if using AI features)
- `UPLOAD_DIR` = `./uploads` (default is fine)

### 2. Render Configuration

Use these settings when creating your Web Service:

```
Name: flicker-microblog (or your preferred name)
Region: Singapore (closest to Bangladesh)
Branch: main
Root Directory: flicker
Runtime: Java
Build Command: ./mvnw clean package -DskipTests
Start Command: java -jar target/*.jar
Instance Type: Free
```

### 3. Important Notes

⚠️ **Free Tier Limitations:**
- App spins down after 15 minutes of inactivity
- First request after spin-down takes 30-60 seconds
- 750 hours/month free

🔒 **Security:**
- Never commit `.env` files or files with secrets
- Always use environment variables for sensitive data
- Rotate exposed credentials immediately

### 4. MongoDB Atlas Setup

Make sure your MongoDB Atlas is configured:
1. Go to Network Access → Add IP Address → Allow Access from Anywhere (0.0.0.0/0)
2. This is required for Render to connect to your database

### 5. Testing Locally with Environment Variables

Create a `flicker/.env` file (git-ignored) with your local values:
```
MONGODB_URI=mongodb+srv://nadim:password@class.xtowejo.mongodb.net/FLICKER?appName=class
OPENAI_API_KEY=your-key-here
PORT=7777
```

Then run:
```bash
# Load env vars and run
export $(cat .env | xargs) && ./mvnw spring-boot:run
```

## Troubleshooting

- **Build fails**: Check Java version is 17+
- **Connection errors**: Verify MongoDB Atlas IP whitelist
- **Slow first load**: Normal for free tier spin-up
