import util.DatabaseInitializer;

/**
 * HIBRET-SYSTEM - Entry Point
 * Main class to start the application
 */
public class Main {

  public static void main(String[] args) {
    // Application entry point
    System.out.println("HIBRET-SYSTEM Starting...");
    DatabaseInitializer.init();

    System.out.println("🚀 System Ready");
  }
}
