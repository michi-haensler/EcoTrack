package at.ecotrack.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * ArchUnit Tests zur Durchsetzung der Modul-Grenzen.
 * Basierend auf den DDD-Architekturregeln aus der PDF.
 */
@DisplayName("Module Architecture Tests")
class ModuleArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("at.ecotrack");
    }

    // ==================== Module Isolation ====================

    @Test
    @DisplayName("Scoring-Modul darf nicht direkt auf UserProfile-Modul zugreifen")
    void scoringModuleShouldNotAccessUserProfileDirectly() {
        noClasses()
                .that().resideInAPackage("..scoring..")
                .and().resideOutsideOfPackage("..scoring.api..")
                .should().dependOnClassesThat().resideInAPackage("..userprofile..")
                .because("Module sollten nur über die öffentliche API kommunizieren")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Challenge-Modul darf nicht direkt auf Scoring-Modul zugreifen (außer Events)")
    void challengeModuleShouldNotAccessScoringDirectly() {
        noClasses()
                .that().resideInAPackage("..challenge..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..scoring.adapter..", "..scoring.application..")
                .because("Module sollten nur über Events oder die öffentliche API kommunizieren")
                .check(importedClasses);
    }

    @Test
    @DisplayName("UserProfile-Modul darf nur auf Scoring-Events zugreifen")
    void userProfileShouldOnlyAccessScoringEvents() {
        noClasses()
                .that().resideInAPackage("..userprofile..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..scoring.adapter..", "..scoring.application..")
                .because("UserProfile reagiert nur auf Scoring-Events")
                .check(importedClasses);
    }

    // ==================== Hexagonal Architecture (Core Domains)
    // ====================

    @Test
    @DisplayName("Scoring Domain-Schicht darf nicht auf Adapter zugreifen")
    void scoringDomainShouldNotAccessAdapters() {
        noClasses()
                .that().resideInAPackage("..scoring.domain..")
                .should().dependOnClassesThat().resideInAPackage("..scoring.adapter..")
                .because("Domain-Schicht ist unabhängig von Infrastruktur (Hexagonal Architecture)")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Scoring Application-Schicht darf nicht auf Adapter zugreifen")
    void scoringApplicationShouldNotAccessAdapters() {
        noClasses()
                .that().resideInAPackage("..scoring.application..")
                .should().dependOnClassesThat().resideInAPackage("..scoring.adapter..")
                .because("Application-Schicht ist unabhängig von Infrastruktur (Hexagonal Architecture)")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Challenge Domain-Schicht darf nicht auf Adapter zugreifen")
    void challengeDomainShouldNotAccessAdapters() {
        noClasses()
                .that().resideInAPackage("..challenge.domain..")
                .should().dependOnClassesThat().resideInAPackage("..challenge.adapter..")
                .because("Domain-Schicht ist unabhängig von Infrastruktur (Hexagonal Architecture)")
                .check(importedClasses);
    }

    // ==================== Shared Kernel ====================

    @Test
    @DisplayName("Shared Kernel darf auf keine Module zugreifen")
    void sharedKernelShouldBeIndependent() {
        noClasses()
                .that().resideInAPackage("..shared..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..scoring..", "..challenge..", "..userprofile..", "..administration..")
                .because("Shared Kernel ist die Basis und darf keine Abhängigkeiten zu Modulen haben")
                .check(importedClasses);
    }

    // ==================== API / Facade ====================

    @Test
    @DisplayName("Modul-Fassaden müssen in .api Paketen liegen")
    void facadesShouldBeInApiPackage() {
        classes()
                .that().haveSimpleNameEndingWith("ModuleFacade")
                .should().resideInAPackage("..api..")
                .because("Modul-APIs müssen klar definiert sein")
                .check(importedClasses);
    }

    // ==================== Controller ====================

    @Test
    @DisplayName("Controller müssen in .controller oder .adapter.in Paketen liegen")
    void controllersShouldBeInCorrectPackage() {
        classes()
                .that().haveSimpleNameEndingWith("Controller")
                .should().resideInAnyPackage("..controller..", "..adapter.in..")
                .because("Controller sind Eingangsadapter")
                .check(importedClasses);
    }

    // ==================== Repository ====================

    @Test
    @DisplayName("JPA Repositories müssen in .adapter.out oder .repository Paketen liegen")
    void repositoriesShouldBeInCorrectPackage() {
        classes()
                .that().haveSimpleNameEndingWith("JpaRepository")
                .should().resideInAnyPackage("..adapter.out..", "..repository..")
                .because("Repositories sind Ausgangsadapter")
                .check(importedClasses);
    }

    // ==================== Anti-Corruption Layer ====================

    @Test
    @DisplayName("Keycloak-Adapter darf nur im Administration-Modul existieren")
    void keycloakAdapterOnlyInAdministration() {
        classes()
                .that().haveSimpleNameContaining("Keycloak")
                .should().resideInAPackage("..administration..")
                .because("Keycloak-Integration ist über ACL im Administration-Modul gekapselt")
                .check(importedClasses);
    }
}
