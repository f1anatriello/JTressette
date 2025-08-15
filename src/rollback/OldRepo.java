package rollback;

/**
 * Vecchia User Repository, la tengo giusto se dovesse servire un rollback
 */

//import java.io.IOException;
//import java.nio.file.*;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class OldRepo {
//
//    private final Path file = Paths.get("utenti.txt");
//
//    public OldRepo() {
//        try {
//            if (Files.notExists(file)) {
//                Files.createFile(file);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Impossibile creare il file utenti", e);
//        }
//    }
//
//    private Set<String> readAll() {
//        try {
//            if (Files.size(file) == 0) return new LinkedHashSet<>();
//            return Files.readAllLines(file).stream()
//                    .map(String::trim)
//                    .filter(s -> !s.isEmpty())
//                    .collect(Collectors.toCollection(LinkedHashSet::new));
//        } catch (IOException e) {
//            throw new RuntimeException("Errore in lettura utenti", e);
//        }
//    }
//
//    private void append(String username) {
//        try {
//            Files.write(file, (username + System.lineSeparator()).getBytes(),
//                    StandardOpenOption.APPEND);
//        } catch (IOException e) {
//            throw new RuntimeException("Errore in scrittura utenti", e);
//        }
//    }
//
//    public static boolean isValidUsername(String u) {
//        if (u == null) return false;
//        String s = u.trim();
//        return s.length() >= 3 && s.length() <= 20 && s.matches("[A-Za-z0-9_.\\- ]+");
//    }
//
//    /** Ritorna True se username esiste già, False se è da creare */
//    public boolean loginOrCreate(String username) {
//        String u = username.trim();
//        Set<String> all = readAll();
//        if (all.contains(u)) {
//            return true;
//        }
//        append(u);
//        return false;
//    }
//}