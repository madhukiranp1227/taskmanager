# 🚀 TaskFlow — Free Deployment Guide

**Frontend → Netlify (free forever)**
**Backend → Render (free, sleeps after 15 min idle)**
**Database → Render PostgreSQL (free)**

---

## 📋 Before You Start

Push your code to GitHub first. If you haven't already:
```bash
cd /Users/pathumad/Desktop/taskmanager
git add .
git commit -m "ready for deployment"
git push origin main
```

---

## STEP 1 — Deploy Backend to Render

### 1.1 Create a PostgreSQL Database on Render
1. Go to **https://render.com** → Sign in with GitHub
2. Click **New** → **PostgreSQL**
3. Name: `taskflow-db`
4. Plan: **Free**
5. Click **Create Database**
6. Copy the **Internal Database URL** (looks like `postgresql://...`)

### 1.2 Deploy the Spring Boot Backend
1. Click **New** → **Web Service**
2. Connect your GitHub repo → select `taskmanager`
3. Settings:
   - **Name**: `taskflow-backend`
   - **Root Directory**: `backend`
   - **Runtime**: Java
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/taskmanager-backend-1.0.0.jar`
   - **Plan**: Free
4. Under **Environment Variables**, add:
   | Key | Value |
   |-----|-------|
   | `SPRING_PROFILES_ACTIVE` | `prod` |
   | `DATABASE_URL` | (paste the PostgreSQL URL from step 1.1) |
   | `JWT_SECRET` | `TaskFlow2024SecureJWTSecretKeyForProduction!` |
5. Click **Create Web Service**
6. Wait for it to build (3-5 min) → you'll get a URL like:
   `https://taskflow-backend.onrender.com`

---

## STEP 2 — Deploy Frontend to Netlify

1. Go to **https://netlify.com** → Sign in with GitHub
2. Click **Add new site** → **Import an existing project**
3. Connect GitHub → select `taskmanager` repo
4. Settings:
   - **Base directory**: `frontend`
   - **Build command**: `npm run build`
   - **Publish directory**: `frontend/dist`
5. Under **Environment Variables**, add:
   | Key | Value |
   |-----|-------|
   | `VITE_API_BASE_URL` | `https://taskflow-backend.onrender.com/api` |
6. Click **Deploy site**
7. You'll get a URL like: `https://taskflow-app.netlify.app`

---

## STEP 3 — Connect Frontend + Backend (CORS)

1. Go back to **Render** → your backend service → **Environment**
2. Add one more variable:
   | Key | Value |
   |-----|-------|
   | `ALLOWED_ORIGINS` | `https://taskflow-app.netlify.app` |
3. Click **Save Changes** → backend will redeploy automatically

---

## ✅ Done! Your app is live at:
- **Frontend**: `https://taskflow-app.netlify.app`
- **Backend API**: `https://taskflow-backend.onrender.com/api`

---

## ⚠️ Free Tier Notes

- **Render free tier**: Backend spins down after **15 minutes of inactivity**
  - First request after idle takes ~30 seconds to wake up
  - Perfect for portfolio demos — just mention it in your README
- **Netlify**: 100% always on, no limitations
- **Render PostgreSQL**: Free for 90 days, then requires upgrade

---

## 🔄 Updating Your App

Every time you push to GitHub main branch:
- **Netlify** auto-rebuilds and deploys the frontend ✅
- **Render** auto-rebuilds and deploys the backend ✅

No manual steps needed after initial setup!
