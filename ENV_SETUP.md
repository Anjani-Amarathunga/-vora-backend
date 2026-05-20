# Environment Setup Guide

## For Local Development

### 1. Create a `.env.local` file in the project root (DO NOT COMMIT THIS)
Copy the `.env.example` file and fill in your local values:

```bash
cp .env.example .env.local
```

### 2. Configure your local database
Update `.env.local` with your MySQL credentials:
```
DB_URL=jdbc:mysql://localhost:3306/evora_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

### 3. Set Environment Variables

**Option A: Using IDE (IntelliJ/VS Code)**
- Go to Run → Edit Configurations
- Add Environment Variables from `.env.local`

**Option B: Using command line (Windows)**
### 3. Running the Application

**EASIEST: Command Line (Recommended for Testing)**

Windows:
```cmd
set SPRING_PROFILES_ACTIVE=dev && mvn spring-boot:run
```

Mac/Linux:
```bash
export SPRING_PROFILES_ACTIVE=dev && mvn spring-boot:run
```

The app now automatically reads `.env.local` from the project root during application startup.

---

**Alternative: IntelliJ IDEA**
1. Click **Run** → **Edit Configurations**
2. Click **+** to add a new configuration
3. Choose **Maven**
4. Name it: `vora-dev`
5. Set **Command line**: `spring-boot:run`
6. Expand **Environment variables** section
7. Add each variable from `.env.local`:
	- `SPRING_PROFILES_ACTIVE=dev`
	- `DB_URL=jdbc:mysql://localhost:3306/evora_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`
	- `DB_USERNAME=your_username`
	- `DB_PASSWORD=your_password`
8. Click **Apply** → **OK**
9. Run using the green play button

**Alternative: VS Code (with EnvFile Extension)**
1. Install extension: **EnvFile** (by Dotenv Organization)
2. In project root, create `.env` file (or use `.env.local`)
3. Open any Spring file → right-click → **Run with EnvFile**

---

## For Production/Dev Server

Set environment variables on the server:
```bash
export SPRING_PROFILES_ACTIVE=prod
export DB_URL=<production_db_url>
export DB_USERNAME=<production_username>
export DB_PASSWORD=<production_password>
export JWT_SECRET=<strong_jwt_secret>
export STRIPE_API_KEY=<stripe_key>
export STRIPE_WEBHOOK_SECRET=<webhook_secret>
```

## Notes

- `.env.local` is already in `.gitignore` — your local secrets will never be committed
- Use the same format for both admin and user services
- Each developer maintains their own `.env.local` with their database credentials
- The application will use environment variables first, then fall back to defaults in `application.yml`
