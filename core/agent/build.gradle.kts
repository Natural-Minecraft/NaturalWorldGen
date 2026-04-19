plugins {
    java
}

tasks.jar {
    manifest.attributes(
        "Agent-Class" to "id.naturalsmp.nwg.toolbelt.agent.Installer",
        "Premain-Class" to "id.naturalsmp.nwg.toolbelt.agent.Installer",
        "Can-Redefine-Classes" to true,
        "Can-Retransform-Classes" to true
    )
}