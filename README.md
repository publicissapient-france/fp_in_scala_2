# Foncteur


Il existe en programmation fonctionnelle des designs patterns comme il en existe en programmation orientée objet.
Cela nous permet de résoudre tous les jours des problèmes similaires avec une même solution en se basant sur l'expérience de la communauté.

Cependant, il existe une différence majeure entre ces deux familles de programmation.
Alors que les designs patterns en OO sont émergents et purement lié à un problème informatique, les designs patterns en programmation fonctionnelle se basent sur les mathématiques!
Oui, il existe une branche des mathématiques qui, indirectement, étudie et prouve l'existence de certaines abstractions!
En effet, ces designs patterns sont souvent très abstraits et font perdre pieds à la plupart des développeurs.
En programmation fonctionnelle, l'unité de raisonnement est la fonction.
Une fonction est une entité capable de transformer des données d'un type A vers un type B.
La théorie des catégories en mathématiques consiste plus ou moins à l'étude des fonctions!


## Petit rappel

Lors de la séance précédente, nous avions créée un type **List** et **Option** avec plusieurs méthodes.

	//LIST
	sealed trait List[+A]{
	    def map[B](f: (A => B)): List[B]
	}

	case object Nil extends List[Nothing]{
	    def map[B](f: (Nothing => B)): List[B] = Nil
	}

	case class Cons[A](a: A, t: List[A]) extends List[A]{
	    def map[B](f: (A => B)): List[B] = Cons(f(a),t.map(f))
	}


	//OPTION
	sealed trait Option[+A]{
		def map[B](f: (A => B)): Option[B]
	}

	case object None extends Option[Nothing]{
		def map[B](f: (Nothing => B)): Option[B] = None
	}

	case class Some[A](a:A) extends Option[A]{
		def map[B](f: (A => B)): Option[B] = Some(f(a))
	}


On remarque rapidement que la méthode **map** a plus ou moins la même signature pour **List** et **Option**. C'est un signe!
Mais il n'est pas encore temps de refactorer car n'existe pas de code commun.


## Exo 1

Codez la méthode fpair sur **List** et **Option** avec la signature suivante:

	sealed trait List[+A]{
	    def fpair[B](f: (A => B)):List[(A,B)]
	}

	sealed trait Option[+A]{
	    def fpair[B](f: (A => B)):Option[(A,B)]
	}

Les types disent tout! **fpair** fait la même chose que **map** mais en sortie, elle associe dans un tuple l'élément de départ (a:A) au résultant de **f**.

## Exo 2

Vous aurez remarqué que le code code écrit dans l'Exo 1 est le même pour List et Option. On peut donc le mettre en commun.

Trouver une interface commune à **List** et **Option** qui ressemble:

	trait Foncteur... {

		def map... (f: (A => B)): ...

		def fpair... (f: (A => B)): ...
	}

Ah oui, il ne doit y avoir aucun warning de compilation, et aucune annotation pour les faire disparaitre!

## Exo 3

Il est impossible de faire proprement l'abstraction précédente, il faut un autre outil.
Scala et Haskell possèdent un outil barbare au nom de "Higher Kinded Type".

Le "kind" est en fait le niveau de généricité d'un type :-)

Niveau 0
Par exemple, Int, String, Boolean, case class Voiture(name: String) sont des types concrets, on peut en instancier sans problèmes.

Niveau 1
List[A], Option[A] sont des types génériques simples. On ne peut les instancier dans ces langages qu'une fois le type générique spécifié.
Si List[A] est de niveau 1, List[String] est de niveau 0, le paramètre générique A étant fixé à String

Et pourquoi s'arrêter içi? En Java, il n'y a pas le choix, mais en Haskell et Scala on peut aller plus loin...

Niveau 2
Si List[A] et Option[A] sont de niveau 1, il peut exister un type en commun qui est F[A], de niveau 2.
Depuis F[A], on peut avoir List[A], Option[A], Future[A]... puis List[String], Option[Voiture], Future[Int]

Pour pouvoir abstraire du code générique entre List et Option, il faut ce type de niveau 2 et voici la signature de l'interface qu'elle devrait avoir:

	trait Foncteur[F[_]] {
	  def map[A, B](fa:F[A])(f: (A => B)): F[B]

	  def fpair[A, B](fa:F[A])(f: (A => B)): F[(A,B)] = ???
	}

L'interface Foncteur n'applique pas la fonction f sur **this** mais sur un autre paramètre *a: F[A]*. F[_] signifie que F est de niveau 2 mais qu'il n'y a pas de contrainte particulière sur le "sous-type" générique, il ne sert à rien de le nommer.

Même si vous n'avez pas encore implémentation de Foncteur, vous pouvez déjà implémenter dans l'interface Foncteur la méthode *fpair* à partir de la méthode abstrait *map*.

## Exo 4

Plutôt que de faire étendre List et Option du Foncteur, on implémente le comportement de Functor d'une List dans une classe anonyme à l'intérieur de l'objet compagnion de List.

	object List {

		val foncteur = new Foncteur[List]{
			def map[A,B](fa: List[A])(f: (A => B)):List[B] = fa.map(f)
		}
	}

On peut même déporter la méthode d'instance *map* ici, cela donne :

	val foncteur = new Foncteur[List] {
		def map[A, B](fa: List[A])(f: (A => B)): List[B] = fa match {
		  case Nil => Nil
		  case Cons(x, t) => Cons(f(x), map(t)(f))
		}
	}


Ce pattern s'appelle "type class". Cela semble étrange mais il est bien plus flexible qu'un héritage classique.
En effet, ce pattern permet aussi d'étendre le comportement de classe existante. Par exemple, il est tout à fait possible de créer une instance de foncteur de String. Nous y reviendrons en détail dans une autre session.

Cependant, en Scala, l'utilisation sous cette forme n'est pas très élégante.

	val maListe = Cons(1,Cons(2,Nil))
	List.foncteur.map(maListe)(_ + 1)
	// Cons(2,Cons(3,Nil))

	List.foncteur.fpair(maListe)(_ + 1)
	// Cons((1,2),Cons((2,3),Nil))

Scala possède un outil permettant décrire

	val maListe = Cons(1,Cons(2,Nil))
	maListe.map(_ + 1)
	maListe.fpair(_ + 1)

Ce sont les extensions de classes, ou classes implicites.

	sealed trait List[+A] ...

	object List{

		val foncteur = new Foncteur[List]{...}

		implicit class FoncteurOps[A](val list:List[A]){
		    def map[B](f: A => B): List[B] = foncteur.map(list)(f)

		    def fpair[B](f: A => B): List[(A,B)] = foncteur.fpair(list)(f)
		  }
	}

En Haskell, cela est fait nativement par le compilateur :-)
C'est l'une des évolutions majeures que Scala devrait prendre dans les prochains mois.


## Exo 5

Les foncteurs reposent sur une définition mathématiques.
Chaque instance de foncteur doit vérifier deux lois pour être utilisée correctement.


Loi1:
Pour toute instance de foncteur, appliquer la fonction identité (x => x) retourne comme résultat la structure d'entrée

	val data = ???
	data.map( x => x ) = x

Loi2:
Soit f une fonction de A => B
Soit g une fonction de B => C
Soit h = g o f une fonction de A => C

Pour toute instance de foncteur, appliquer la fonction h au foncteur est équivalent à appliquer g sur le résultat de l'application de f au foncteur.

	val f: (A => B) = ???
	val g: (B => C) = ???

	val data = ???
	data.map(h) === data.map(f).map(g)


Écrire les tests unitaires pour les foncteurs List dans ListFunctorSpec. Vous trouverez le support de Scalacheck qui permet de générer des listes à taille variable.


Question bonus:
Comment écrire un test unitaire générique sur fpair générique? En fait, il est possible de le créer en utilisant soit une instance existante (genre List) ou alors en en créant une anonyme pour le test.
Mais c'est là la beauté des mathématiques. Si vous instances de Foncteur vérifie les deux lois d'identité et d'associativité, alors fpair est sûre de fonctionner, quelque soit le type de *A*.
C'est pas beautiful ca?