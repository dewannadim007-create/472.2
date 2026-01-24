# MongoDB Connection Error - Fix Summary

## Problem Identified

Your application was failing to deploy on Render.com with the following error:

```
com.mongodb.MongoConfigurationException: Failed looking up SRV record for '_mongodb._tcp.pulse-cluster.tmwyu.mongodb.net'.
Caused by: javax.naming.NameNotFoundException: DNS name not found [response code 3]
```

### Root Causes:

1. **Hardcoded MongoDB credentials** in `application.properties` (security issue)
2. **Connection string included `appName` parameter** which can cause DNS resolution issues
3. **No environment variable configuration** for deployment platforms like Render.com

## Solution Applied

### 1. Changed `application.properties` to Use Environment Variables

**Before:**
```properties
spring.data.mongodb.uri=mongodb+srv://PulseAdmin:iZ6X8Wdbk4iO8KHS@pulse-cluster.tmwyu.mongodb.net/flicker?retryWrites=true&w=majority&appName=Pulse-Cluster
server.port=8080
app.upload.dir=uploads
```

**After:**
```properties
# Use MONGODB_URI environment variable with fallback for local development
spring.data.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/flicker}

# Use PORT environment variable (Render sets this automatically)
server.port=${PORT:8080}

# Use UPLOAD_DIR environment variable
app.upload.dir=${UPLOAD_DIR:uploads}
```

### 2. Benefits of This Change:

✅ **Security**: No hardcoded credentials in source code  
✅ **Flexibility**: Works in both local and production environments  
✅ **Render Compatible**: Uses PORT environment variable that Render provides  
✅ **DNS Safe**: Removes problematic `appName` parameter

## Action Required: Set Environment Variables in Render

### CRITICAL: Update Your MongoDB Password

Since your credentials were exposed in the code, you MUST:

1. **Go to MongoDB Atlas** (https://cloud.mongodb.com)
2. Navigate to **Database Access**
3. Edit your user (`PulseAdmin`) and **set a NEW password**
4. Save the new password securely

### Set Environment Variables in Render

1. **Go to your Render Dashboard**: https://dashboard.render.com
2. **Select your web service** (flicker-microblog or similar)
3. **Navigate to Environment** tab
4. **Add/Update these environment variables**:

| Variable Name | Example Value | Notes |
|---------------|---------------|-------|
| `MONGODB_URI` | `mongodb+srv://PulseAdmin:YOUR_NEW_PASSWORD@pulse-cluster.tmwyu.mongodb.net/flicker?retryWrites=true&w=majority` | Use your NEW password, database name is 'flicker' (lowercase) |
| `PORT` | `10000` | Render sets this automatically |
| `UPLOAD_DIR` | `/tmp/uploads` | For file uploads in cloud |
| `CLOUDINARY_CLOUD_NAME` | Your value | If using Cloudinary |
| `CLOUDINARY_API_KEY` | Your value | If using Cloudinary |
| `CLOUDINARY_API_SECRET` | Your value | If using Cloudinary |

### Important Notes for MONGODB_URI:

⚠️ **Remove the `appName` parameter** - it can cause DNS issues:
- ✅ GOOD: `...mongodb.net/flicker?retryWrites=true&w=majority`
- ❌ BAD: `...mongodb.net/flicker?retryWrites=true&w=majority&appName=Pulse-Cluster`

⚠️ **URL encode special characters** in your password:
- If password contains `@` → use `%40`
- If password contains `#` → use `%23`
- If password contains `:` → use `%3A`

⚠️ **Verify Network Access** in MongoDB Atlas:
- Go to **Network Access** tab
- Ensure `0.0.0.0/0` is in the IP Access List (allows connections from anywhere)

## Testing the Fix

### For Local Development:

```bash
# Option 1: Run with local MongoDB (no environment variable needed)
cd flicker
./mvnw spring-boot:run

# Option 2: Run with MongoDB Atlas
export MONGODB_URI="mongodb+srv://username:password@cluster.mongodb.net/flicker?retryWrites=true&w=majority"
./mvnw spring-boot:run
```

### For Render Deployment:

1. Commit and push your changes (already done in this PR)
2. Set the environment variables in Render (see above)
3. Trigger a manual deploy or wait for auto-deploy
4. Check logs in Render Dashboard to verify successful startup
5. Look for: `Started PulseApplication in X seconds`

## Expected Behavior After Fix

### Success Indicators:

✅ Application starts without MongoDB connection errors  
✅ No DNS resolution failures  
✅ Admin user is created automatically  
✅ Application responds to web requests

### Startup Logs Should Show:

```
INFO ... : Started PulseApplication in 8.234 seconds
INFO ... : Tomcat started on port 10000
```

### If Problems Persist:

1. **Double-check MongoDB Atlas Network Access**
   - Must allow `0.0.0.0/0`

2. **Verify connection string format**
   - Database name must be present: `...mongodb.net/flicker?...`
   - No `appName` parameter

3. **Check Render logs for specific errors**
   - Dashboard → Your Service → Logs tab

4. **Test connection string locally first**
   ```bash
   export MONGODB_URI="your-connection-string"
   cd flicker
   ./mvnw spring-boot:run
   ```

## Additional Improvements Made

1. **Updated README.md** with proper environment variable configuration instructions
2. **Enhanced DEPLOYMENT.md** with detailed troubleshooting steps
3. **Added security warnings** about hardcoded credentials
4. **Documented URL encoding** for special characters in passwords

## Files Changed

- ✅ `flicker/src/main/resources/application.properties` - Use environment variables
- ✅ `README.md` - Updated configuration instructions
- ✅ `flicker/DEPLOYMENT.md` - Enhanced troubleshooting guide

## Next Steps

1. ✅ Code changes are complete (in this PR)
2. ⚠️ **YOU MUST**: Change MongoDB password in Atlas
3. ⚠️ **YOU MUST**: Set environment variables in Render
4. ⚠️ **YOU MUST**: Redeploy application in Render
5. ✅ Verify application starts successfully
6. ✅ Test basic functionality

## Security Best Practices Going Forward

- ✅ Never commit credentials to source code
- ✅ Always use environment variables for sensitive configuration
- ✅ Rotate credentials immediately if exposed
- ✅ Use `.env` files locally (add to `.gitignore`)
- ✅ Document required environment variables in README

---

**Need Help?** 
- MongoDB Atlas Docs: https://docs.atlas.mongodb.com
- Render Docs: https://render.com/docs
- Spring Boot Environment Variables: https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config
