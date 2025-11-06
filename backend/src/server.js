import express from "express";
import cors from "cors";
import "dotenv";

console.log("FRONTEND_URL =", process.env.FRONTEND_URL);

const app = express();

const BACKEND_PORT = Number(process.env.BACKEND_PORT) || 5000;

/**
 * CORS
 * Allow the Vite dev server (or your deployed frontend) to send cookies.
 * - origin: FRONTEND_URL
 * - credentials: true to allow cookie-based auth
 *
 * NOTE: Place CORS before your routes.
 */
app.use(
  cors({
    origin: process.env.FRONTEND_URL,
    credentials: true,
  })
);

/**
 * Core middleware
 * - JSON body parser for application/json
 * - cookieParser so we can read req.cookies.token in auth flows
 */
app.use(express.json());
app.use(cookieParser());

// routes

app.use("/auth", authRoutes);

/**
 * 404 handler for unmapped routes
 * Keep after your route mounts.
 */
app.use((req, res) => {
  res
    .status(404)
    .json({ error: `Not found: ${req.method} ${req.originalUrl}` });
});

/**
 * Basic error handler
 * Catches unexpected errors and returns a consistent JSON shape.
 */
app.use((err, _req, res, _next) => {
  console.error("Unhandled error:", err);
  res.status(500).json({ error: "Internal server error" });
});

// start server
app.listen(BACKEND_PORT, () => {
  console.log(`Server running on port ${BACKEND_PORT}`);
});
