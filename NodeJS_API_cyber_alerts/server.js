const express = require("express");
const mongoose = require("mongoose");
const bodyParser = require("body-parser");
const cors = require("cors");

const app = express();

// Middlewares
app.use(bodyParser.json());
app.use(cors());

// MongoDB Configuración
mongoose.connect("mongodb://localhost:27017/cyber-alerts", {
  useNewUrlParser: true,
  useUnifiedTopology: true,
});
const db = mongoose.connection;
db.on("error", console.error.bind(console, "MongoDB connection error:"));
db.once("open", () => console.log("MongoDB connected"));

// Esquema y Modelo de Alertas
const alertSchema = new mongoose.Schema({
  type: String,
  message: String,
  timestamp: { type: Date, default: Date.now },
});
const Alert = mongoose.model("Alert", alertSchema);

// Rutas
app.post("/alerts", async (req, res) => {
  try {
    const alert = new Alert(req.body);
    await alert.save();
    res.status(201).json({ success: true, alert });
  } catch (error) {
    res.status(500).json({ success: false, error });
  }
});

app.get("/alerts", async (req, res) => {
  try {
    const alerts = await Alert.find();
    res.status(200).json({ success: true, alerts });
  } catch (error) {
    res.status(500).json({ success: false, error });
  }
});

// Iniciar Servidor
const PORT = 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
