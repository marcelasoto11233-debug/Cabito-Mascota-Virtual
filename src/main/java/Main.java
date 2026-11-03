package com.cabito;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.Properties;

public class Main extends Application {
    private final Stats stats = new Stats();
    private int coins = 30, level = 1, xp = 0;
    private Label coinLabel, levelLabel, moodLabel, messageLabel;
    private ProgressBar healthBar, hungerBar, energyBar, happinessBar, hygieneBar, xpBar;
    private ImageView cabitoView;
    private Timeline clock;
    private static final String SAVE_FILE = "cabito-partida.properties";

    @Override public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(22));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #211b3d, #38275a);");

        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);
        Label logo = new Label("✨ CABITO");
        logo.setFont(Font.font("System", 28));
        logo.setTextFill(Color.web("#FFE28A"));
        coinLabel = new Label();
        coinLabel.setStyle(chipStyle("#F7C95C", "#332447"));
        levelLabel = new Label();
        levelLabel.setStyle(chipStyle("#BDA4FF", "#2C2049"));
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(logo, spacer, levelLabel, coinLabel);
        root.setTop(header);

        VBox center = new VBox(14);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(12, 0, 8, 0));

        Label name = new Label("¡Hola! Soy Cabito");
        name.setTextFill(Color.WHITE); name.setFont(Font.font("System", 25));
        moodLabel = new Label("Estado: NORMAL");
        moodLabel.setTextFill(Color.web("#FFE28A")); moodLabel.setFont(Font.font(15));

        StackPane petFrame = new StackPane();
        petFrame.setMaxSize(390, 400);
        petFrame.setMinSize(280, 300);
        petFrame.setStyle("-fx-background-color: rgba(255,255,255,0.10); -fx-background-radius: 28; -fx-border-color: rgba(255,255,255,0.22); -fx-border-radius: 28;");
        try {
            Image image = new Image(getClass().getResourceAsStream("/images/cabito.png"));
            cabitoView = new ImageView(image);
            cabitoView.setPreserveRatio(true);
            cabitoView.setFitHeight(370);
            cabitoView.setFitWidth(350);
            petFrame.getChildren().add(cabitoView);
        } catch (Exception ex) {
            Label fallback = new Label("🦊\\nCABITO");
            fallback.setTextFill(Color.WHITE); fallback.setFont(Font.font(42));
            petFrame.getChildren().add(fallback);
        }

        messageLabel = new Label("¡Qué alegría verte! Cuídame mucho 💛");
        messageLabel.setTextFill(Color.WHITE); messageLabel.setFont(Font.font(15));
        center.getChildren().addAll(name, moodLabel, petFrame, messageLabel);
        root.setCenter(center);

        VBox right = new VBox(12);
        right.setPadding(new Insets(18));
        right.setPrefWidth(260);
        right.setStyle("-fx-background-color: rgba(17,13,34,0.55); -fx-background-radius: 22;");
        Label statsTitle = sectionTitle("📊 MIS ESTADÍSTICAS");
        healthBar = addStat(right, "❤️ Salud", "#F07891");
        hungerBar = addStat(right, "🍎 Hambre", "#F4C45E");
        energyBar = addStat(right, "⚡ Energía", "#80D7E8");
        happinessBar = addStat(right, "😊 Felicidad", "#8DE0B2");
        hygieneBar = addStat(right, "🧼 Higiene", "#BDA4FF");
        Label xpTitle = sectionTitle("⭐ EXPERIENCIA");
        xpBar = new ProgressBar(0);
        xpBar.setMaxWidth(Double.MAX_VALUE);
        xpBar.setStyle("-fx-accent: #F5D16B;");
        right.getChildren().addAll(statsTitle, xpTitle, xpBar);
        root.setRight(right);

        VBox bottom = new VBox(12);
        FlowPane actions = new FlowPane(10, 10);
        actions.setAlignment(Pos.CENTER);
        actions.getChildren().addAll(
            actionButton("🍎 Alimentar", "#E7A64B", () -> feed()),
            actionButton("💤 Dormir", "#8B79C9", () -> sleep()),
            actionButton("🎮 Jugar", "#E47D9A", () -> play()),
            actionButton("🧼 Bañar", "#65B9C9", () -> bathe()),
            actionButton("🏋️ Entrenar", "#69B58E", () -> train()),
            actionButton("🛍️ Tienda", "#D69B55", () -> shop())
        );
        HBox system = new HBox(10);
        system.setAlignment(Pos.CENTER);
        system.getChildren().addAll(
            actionButton("💾 Guardar", "#596C9D", () -> save()),
            actionButton("📂 Cargar", "#596C9D", () -> load()),
            actionButton("🌳 Parque", "#548C70", () -> message("Cabito está disfrutando del parque 🌳"))
        );
        bottom.getChildren().addAll(actions, system);
        root.setBottom(bottom);
        BorderPane.setMargin(right, new Insets(12, 0, 12, 18));
        BorderPane.setMargin(center, new Insets(10));

        updateUI();
        load(false);
        clock = new Timeline(new KeyFrame(Duration.seconds(8), e -> {
            stats.hunger = clamp(stats.hunger + 2);
            stats.energy = clamp(stats.energy - 1);
            stats.hygiene = clamp(stats.hygiene - 1);
            if (stats.hunger > 90 || stats.energy < 10 || stats.hygiene < 10) stats.health = clamp(stats.health - 2);
            updateUI();
        }));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();

        Scene scene = new Scene(root, 1100, 780);
        stage.setTitle("CABITO - Mascota Virtual");
        stage.setMinWidth(850); stage.setMinHeight(700);
        stage.setScene(scene); stage.show();
    }

    private String chipStyle(String bg, String fg) {
        return "-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-font-weight: bold; -fx-padding: 9 14; -fx-background-radius: 18;";
    }
    private Label sectionTitle(String text) {
        Label l = new Label(text); l.setTextFill(Color.web("#FFE28A")); l.setFont(Font.font("System", 15)); return l;
    }
    private ProgressBar addStat(VBox box, String title, String color) {
        Label l = new Label(title); l.setTextFill(Color.WHITE);
        ProgressBar b = new ProgressBar(0); b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-accent: " + color + ";");
        box.getChildren().addAll(l, b); return b;
    }
    private Button actionButton(String text, String color, Runnable task) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 16; -fx-padding: 12 17; -fx-cursor: hand;");
        b.setOnAction(e -> { task.run(); updateUI(); });
        return b;
    }
    private void feed() {
        if (coins < 5) { message("Necesito más monedas para comprar comida 🪙"); return; }
        coins -= 5; stats.hunger = clamp(stats.hunger - 25);
        stats.happiness = clamp(stats.happiness + 5); stats.health = clamp(stats.health + 2);
        message("¡Qué rico! Gracias por alimentarme 🍎");
    }
    private void sleep() {
        stats.energy = clamp(stats.energy + 35);
        stats.health = clamp(stats.health + 5);
        message("Zzz... ¡Estoy recuperando energía! 💤");
    }
    private void play() {
        if (stats.energy < 10) { message("Estoy demasiado cansado para jugar. ¡Necesito dormir!"); return; }
        stats.energy = clamp(stats.energy - 10);
        stats.happiness = clamp(stats.happiness + 20);
        earnXp(5); coins += 3;
        message("¡Me divertí muchísimo jugando contigo! 🎮 +3 monedas");
    }
    private void bathe() {
        stats.hygiene = clamp(stats.hygiene + 40);
        stats.happiness = clamp(stats.happiness + 5);
        message("¡Quedé limpio y brillante! 🫧");
    }
    private void train() {
        if (stats.energy < 15) { message("No tengo energía para entrenar. ¡A descansar!"); return; }
        stats.energy = clamp(stats.energy - 15);
        earnXp(15); coins += 5;
        message("¡Entrenamiento completado! +15 XP y +5 monedas 💪");
    }
    private void shop() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Tienda de Cabito"); a.setHeaderText("Tienda mágica 🛍️");
        a.setContentText("Comprar snack especial por 10 monedas. ¿Deseas continuar?");
        a.showAndWait().ifPresent(button -> {
            if (button == ButtonType.OK) {
                if (coins >= 10) { coins -= 10; stats.hunger = clamp(stats.hunger - 40); stats.happiness = clamp(stats.happiness + 10); message("¡El snack estaba delicioso!"); }
                else message("No tienes suficientes monedas.");
                updateUI();
            }
        });
    }
    private void earnXp(int amount) {
        xp += amount;
        while (xp >= 100) { xp -= 100; level++; coins += 15; message("🎉 ¡Subí al nivel " + level + "! Recibiste 15 monedas."); }
    }
    private void updateUI() {
        healthBar.setProgress(stats.health / 100.0);
        hungerBar.setProgress(stats.hunger / 100.0);
        energyBar.setProgress(stats.energy / 100.0);
        happinessBar.setProgress(stats.happiness / 100.0);
        hygieneBar.setProgress(stats.hygiene / 100.0);
        xpBar.setProgress(xp / 100.0);
        coinLabel.setText("🪙 " + coins);
        levelLabel.setText("⭐ Nivel " + level);
        String mood = stats.hunger > 80 ? "HAMBRIENTO 😠" : stats.energy < 20 ? "CANSADO 😴" :
            stats.happiness > 70 ? "FELIZ 😄" : stats.happiness < 30 ? "TRISTE 😢" : "NORMAL 🤓";
        moodLabel.setText("Estado: " + mood);
    }
    private void message(String text) { messageLabel.setText(text); }
    private int clamp(int value) { return Math.max(0, Math.min(100, value)); }

    private void save() {
        Properties p = new Properties();
        p.setProperty("health", ""+stats.health); p.setProperty("hunger", ""+stats.hunger);
        p.setProperty("energy", ""+stats.energy); p.setProperty("happiness", ""+stats.happiness);
        p.setProperty("hygiene", ""+stats.hygiene); p.setProperty("coins", ""+coins);
        p.setProperty("level", ""+level); p.setProperty("xp", ""+xp);
        try (OutputStream out = new FileOutputStream(SAVE_FILE)) {
            p.store(out, "Partida de Cabito"); message("¡Partida guardada correctamente! 💾");
        } catch (IOException ex) { message("No se pudo guardar: " + ex.getMessage()); }
    }
    private void load() { load(true); }
    private void load(boolean showMessage) {
        Properties p = new Properties();
        File file = new File(SAVE_FILE);
        if (!file.exists()) { if (showMessage) message("Todavía no hay una partida guardada."); return; }
        try (InputStream in = new FileInputStream(file)) {
            p.load(in);
            stats.health = read(p,"health",100); stats.hunger = read(p,"hunger",50);
            stats.energy = read(p,"energy",100); stats.happiness = read(p,"happiness",50);
            stats.hygiene = read(p,"hygiene",100); coins = read(p,"coins",30);
            level = read(p,"level",1); xp = read(p,"xp",0);
            if (showMessage) message("¡Tu partida fue recuperada! 💜");
            updateUI();
        } catch (IOException | NumberFormatException ex) { message("No se pudo cargar la partida."); }
    }
    private int read(Properties p, String key, int fallback) { return Integer.parseInt(p.getProperty(key, ""+fallback)); }

    @Override public void stop() { if (clock != null) clock.stop(); }

    private static class Stats {
        int health = 100, hunger = 50, energy = 100, happiness = 70, hygiene = 100;
    }
    public static void main(String[] args) { launch(args); }
}
