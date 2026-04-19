plugins {
    java
}

tasks.jar {
    manifest.attributes(
        "Agent-Class" to "id.naturalsmp.NaturalWorldGen.util.agent.Installer",
        "Premain-Class" to "id.naturalsmp.NaturalWorldGen.util.agent.Installer",
        "Can-Redefine-Classes" to true,
        "Can-Retransform-Classes" to true
    )
}