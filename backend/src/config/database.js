import "dotenv/config";
import mysql from "mysql2/promise";

console.log("Connecting to", `${process.env.DB_HOST}:${process.env.DB_PORT}`);

const pool = mysql.createPool({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
  port: Number(process.env.DB_PORT),
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0,
});

pool
  .getConnection()
  .then((conn) => {
    console.log("Successfully connected to db");
    conn.release();
  })
  .catch((err) => {
    console.log("Error connecting to database", err);
  });

export default pool;
