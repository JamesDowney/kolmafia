package net.sourceforge.kolmafia.session;

import static internal.helpers.Player.withClass;
import static internal.helpers.Player.withPath;
import static org.junit.jupiter.api.Assertions.assertEquals;

import internal.helpers.Cleanups;
import java.util.stream.Stream;

import internal.network.FakeHttpClientBuilder;
import net.sourceforge.kolmafia.AscensionClass;
import net.sourceforge.kolmafia.AscensionPath;
import net.sourceforge.kolmafia.KoLCharacter;
import net.sourceforge.kolmafia.KoLmafia;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class GuildUnlockManagerTest {

  @BeforeAll
  public static void beforeAll() {
    KoLCharacter.reset("GuildUnlock");
  }

  public static Stream<Arguments> playerStates() {
    return Stream.of(
        Arguments.of(AscensionClass.TURTLE_TAMER, AscensionPath.Path.STANDARD, true, true),
        Arguments.of(AscensionClass.SAUCEROR, AscensionPath.Path.POKEFAM, false, false),
        Arguments.of(AscensionClass.PASTAMANCER, AscensionPath.Path.YOU_ROBOT, false, false),
        Arguments.of(AscensionClass.ED, AscensionPath.Path.ACTUALLY_ED_THE_UNDYING, false, false));
  }

  @ParameterizedTest
  @MethodSource("playerStates")
  public void canDetermineGuildAvailability(
      AscensionClass playerClass, AscensionPath.Path playerPath, boolean expectedResult) {
    var cleanups =
        new Cleanups(withClass(AscensionClass.SEAL_CLUBBER), withPath(AscensionPath.Path.NONE));
    try (cleanups) {
      KoLCharacter.setAscensionClass(playerClass);
      KoLCharacter.setPath(playerPath);
      assertEquals(GuildUnlockManager.canUnlockGuild(), expectedResult);
    }
  }

  @ParameterizedTest
  @MethodSource("playerStates")
  public void canSuccessfullyUnlockGuild(
          AscensionClass playerClass, AscensionPath.Path playerPath, boolean expectedResult, boolean guildStoreOpen) {
      var builder = new FakeHttpClientBuilder();
      var cleanups = new Cleanups(
              withClass(AscensionClass.SEAL_CLUBBER), withPath(AscensionPath.Path.NONE)
      );
      try(cleanups) {
          KoLCharacter.setAscensionClass(playerClass);
          KoLCharacter.setPath(playerPath);
          KoLCharacter.setGuildStoreOpen(guildStoreOpen);
          GuildUnlockManager.unlockGuild();
          assertEquals(KoLmafia.getLastMessage(), "Guild already unlocked.");
      }
  }


}
