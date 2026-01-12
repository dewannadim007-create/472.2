# Deployment Guide - Render.com

This guide will help you deploy the Flicker Spring Boot application to Render.com's free tier.

## Prerequisites

1. **GitHub Account** - Your code must be in a GitHub repository
2. **MongoDB Atlas Account** - [Sign up here](https://www.mongodb.com/cloud/atlas/register)
3. **Render.com Account** - [Sign up here](https://render.com/)
4. **OpenAI API Key** (if using AI features) - [Get one here](https://platform.openai.com/api-keys)

## Step 1: Set Up MongoDB Atlas

1. Go to [MongoDB Atlas](https://cloud.mongodb.com)
2. Create a free cluster (M0 tier)
3. Go to **Database Access** → Create a database user
4. Go to **Network Access** → Add IP Address → **Allow Access from Anywhere** (0.0.0.0/0)
5. Go to **Database** → **Connect** → **Connect your application**
6. Copy your connection string (looks like: `mongodb+srv://username:password@cluster.mongodb.net/`)
7. Replace `<password>` with your actual password
8. Add `/FLICKER` before the `?` to specify the database name

Example: `mongodb+srv://nadim:yourpassword@cluster.mongodb.net/FLICKER?retryWrites=true&w=majority`

## Step 2: Prepare Your Repository

### Update Exposed Credentials (URGENT)

Your MongoDB password and OpenAI key were exposed in the previous commit. You should:

1. **Change your MongoDB password**:
   - Go to MongoDB Atlas → Database Access
   - Edit your user and set a new password
   
2. **Rotate your OpenAI API key**:
   - Go to [OpenAI API Keys](https://platform.openai.com/api-keys)
   - Revoke the exposed key
   - Create a new one

### Push Changes

```bash
git add .
git commit -m "Prepare for Render deployment with environment variables"
git push origin main
```

## Step 3: Deploy to Render.com

### 3.1 Create Web Service

1. Go to [Render Dashboard](https://dashboard.render.com/)
2. Click **"New +"** → **"Web Service"**
3. Select **"Build and deploy from a Git repository"**
4. Click **"Connect GitHub"** and authorize Render
5. Select your repository: `dewannadim007-create/advanced_java_project`
6. Click **"Connect"**

### 3.2 Configure Service

Fill in the following settings:

| Setting | Value |
|---------|-------|
| **Name** | `flicker-microblog` (or your choice) |
| **Region** | Singapore (closest to Bangladesh) |
| **Branch** | `main` |
| **Root Directory** | `flicker` |
| **Runtime** | `Java` |
| **Build Command** | `./mvnw clean package -DskipTests` |
| **Start Command** | `java -jar target/*.jar` |
| **Instance Type** | `Free` |

### 3.3 Add Environment Variables

Click **"Advanced"** → **"Add Environment Variable"** and add:

| Key | Value |
|-----|-------|
| `MONGODB_URI` | Your MongoDB Atlas connection string |
| `PORT` | `10000` (Render's default) |
| `UPLOAD_DIR` | `/tmp/uploads` |
| `OPENAI_API_KEY` | Your OpenAI API key (if using AI features) |

**Example MONGODB_URI**:
```
mongodb+srv://nadim:NEW_PASSWORD@cluster.mongodb.net/FLICKER?retryWrites=true&w=majority
```

### 3.4 Deploy

1. Click **"Create Web Service"**
2. Render will:
   - Clone your repository
   - Run Maven build
   - Start your application
   - Provide a URL like: `https://flicker-microblog.onrender.com`

⏱️ **First deployment takes 5-10 minutes**

## Step 4: Verify Deployment

1. Check the **Logs** tab in Render dashboard
2. Look for: `Started FlickerApplication in X seconds`
3. Visit your app URL: `https://your-app-name.onrender.com`

## Important: Render Free Tier Limitations

⚠️ **Free tier apps spin down after 15 minutes of inactivity**
- First request after spin-down takes 30-60 seconds to respond
- Subsequent requests will be fast
- You get 750 hours/month free (enough for 1 app running 24/7)

## Troubleshooting

### Build Fails

**Error**: `Permission denied: ./mvnw`

**Solution**: Make the Maven wrapper executable:
```bash
git update-index --chmod=+x flicker/mvnw
git commit -m "Make mvnw executable"
git push
```

### Database Connection Fails

**Error**: `MongoSocketException` or `Connection refused`

**Solutions**:
1. Verify MongoDB Atlas allows connections from `0.0.0.0/0`
2. Check your connection string format
3. Ensure password doesn't contain special characters (use URL encoding)
4. Verify database name is in the connection string

### App Crashes on Startup

**Check logs for**:
- Port binding issues → Ensure `server.port=${PORT:8080}` is set
- Missing environment variables → Verify all env vars are set in Render
- Dependency issues → Check Java version (should be 17)

### File Upload Issues

On cloud platforms like Render, use `/tmp/uploads` instead of local paths like `C:/uploads`.

Set `UPLOAD_DIR=/tmp/uploads` in environment variables.

## Monitoring Your App

### View Logs
- Go to Render Dashboard → Your Service → **Logs** tab
- Real-time logs show requests, errors, and application output

### Keep App Alive
Free tier apps sleep after inactivity. To keep alive:
- Use a service like [UptimeRobot](https://uptimerobot.com/) to ping every 10 minutes
- Or accept the 30-second cold start delay

## Custom Domain (Optional)

1. Purchase a domain (e.g., from Namecheap, GoDaddy)
2. In Render: Settings → Custom Domain
3. Add your domain and configure DNS records
4. Render provides free SSL certificates!

## Next Steps

- ✅ Monitor your application logs
- ✅ Set up MongoDB backups in Atlas
- ✅ Consider upgrading to a paid tier for no cold starts
- ✅ Add application monitoring (New Relic, Datadog)

## Support

- [Render Documentation](https://render.com/docs)
- [Spring Boot Deployment Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html)
- [MongoDB Atlas Documentation](https://docs.atlas.mongodb.com/)

---

**🎉 Congratulations!** Your Flicker app is now live on the internet!
