package org.rewedigital.katana.comparison

import de.earley.kompanionDI.Context
import de.earley.kompanionDI.KompanionDI
import de.earley.kompanionDI.Provider
import de.earley.kompanionDI.providers.singleton

typealias Profile = Unit

class KompanionSubject : Subject {

    // type bridge
    val inject: Context<DI, Profile>
        get() = KompanionDI.getInject()

    class DI {
        private val mySingleton: Provider<MySingleton, Profile> = singleton { _, _ -> MySingleton() }
        val myDependency: Provider<MyDependency, Profile> = { _, inject -> MyDependencyImpl(inject(mySingleton)) }
        val myDependency2: Provider<MyDependency2, Profile> = { _, inject -> MyDependency2(inject(mySingleton)) }
    }

    override fun setup() {
        KompanionDI.setupDI(Context.create(DI()))
    }

    override fun execute() {
        val dependency = inject { myDependency }
        val dependency2 = inject { myDependency2 }

        dependency.execute()
        dependency2.execute()
    }

    override fun shutdown() {
        return

    }


}