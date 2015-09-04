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

Codez la méthode fproduct sur **List** et **Option** avec la signature suivante:

	sealed trait List[+A]{
	    def fpair[B](f: (A => B)):List[(A,B)]
	}

	sealed trait Option[+A]{
	    def fpair[B](f: (A => B)):Option[(A,B)]
	}

Les types disent tout! **fproduct** fait la même chose que **map** mais en sortie, elle associe dans un tuple l'élément de départ (a:A) au résultant de **f**.

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

	  def fproduct[A, B](fa:F[A])(f: (A => B)): F[(A,B)] = ???
	  
	  def fpair[A, B](fa: F[A]): F[(A, A)] = map(fa)(a => (a, a))
	}

L'interface Foncteur n'applique pas la fonction f sur **this** mais sur un autre paramètre *a: F[A]*. F[_] signifie que F est de niveau 2 mais qu'il n'y a pas de contrainte particulière sur le "sous-type" générique, il ne sert à rien de le nommer.

Vous remarquez qu'il existe ainsi déjà une fonction qui se nomme **fpair**. Elle est implémentée donc disponible pour toutes les instances de foncteurs (List, Option ...). 

En s'en inspirant, implémentez la méthode **fproduct**.

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

En Haskell, cela est fait nativement par le compilateur :-)
C'est l'une des évolutions majeures que Scala devrait prendre dans les prochains mois.

Une session sera dédiée au Type Class plus tard.

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
Comment écrire un test unitaire générique sur fproduct générique? En fait, il est possible de le créer en utilisant soit une instance existante (genre List) ou alors en en créant une anonyme pour le test.
Mais c'est là la beauté des mathématiques. Si vous instances de Foncteur vérifie les deux lois d'identité et d'associativité, alors fproduct est sûre de fonctionner, quelque soit le type de *A*.
C'est pas beautiful ca?

# Exo 6

**fproduct** n'est pas la seule méthode à venir avec le foncteur. Voici d'autres méthodes qui existe avec la librarie Scalaz pour tout foncteur:

  	def mapply[A, B](a : A)(f : F[A => B]) : F[B]
	def lift[A, B](f : A => B) : F[A] => F[B]
	
Ces implémentations sont un peu plus difficile. Mais les signatures sont votre ami.
Même sans avoir aucune idée de ce qu'elles font, implémenter ces deux méthodes dans la classe foncteur. Vous aller expérimenter le Type Driven Development. C'est le compilateur et les types qui vous guident dans l'implémentation. Si ça compile, c'est que ça marche!
 

NB: papier et crayon sont vos amis. Oubliez les notions classe, de méthode et de variable, pensez TRANSFORMATION depuis une variable d'un type vers un autre.

NB: **lift** est ce qu'on appelle une fonction d'ordre supérieure. C'est simplement une fonction qui retourne une autre fonction. 
Nous vous conseillons fortement de commencer votre ligne d'implémentation par *(fa:F[A]) =>*, le reste devrait venir tout seul avec ce dont vous avez sous la main.

Il y a encore d'autres méthodes utilitaires dans Scalaz sur les foncteurs. Parcourez la classe **scalaz.Functor** si vous souhaitez en savoir plus.

En effectuant des tests unitaires avec l'instance de List, on se rend compte qu'on peut soit appliquer une seule fonction à une liste d'éléments, ou une liste de fonctions à un seul élément. Et si on voulait appliquer une liste de fonctions à une liste d'éléments. Le foncteur seul ne suffit plus.
 
# Exo 7

Une autre abstraction est l'**Foncteur Applicatif** ou Applicative Functor. C'est une spécialisation du **Foncteur** qui doit répondre à des lois supplémentaires mais offrent en contrepartie plus de méthodes utilitaires.

 
Voici sa signature:
 
	trait Applicative[F[_]] extends Foncteur[F]{
        
		def point[A](a: A): F[A]
    
		def ap[A, B](fa: F[A])(f: F[A => B]): F[B]
		
		override def map[A, B](fa: F[A])(f: (A => B)): F[B] = ???
	}

L'instance d'applicative possède deux méthodes abstraites, **point** et **ap**. Il est cependant possible d'écrire l'implémentation de **map** définie par foncteur à partir de ces deux méthodes.
	
Écrivez l'implémentation de **map** puis l'instance d'Applicative pour List et Option.

# Exo 8
À ce stade, l'applicative ne fournit rien de plus que le foncteur. Dans l'interface **Applicative**, implémenter la méthode.

	def ap2[A, B, C](fa: F[A], fb: F[B])(f: F[(A, B) => C]): F[C]
	
Vous devez fortement utiliser la méthode **ap**. Attention, c'est hard!
	
Utilisez ensuite cette définition pour implémenter les méthodes suivantes:
	
	def apply2[A, B, C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C]
	
	def mapply2[A, B, C](a: A, b: B)(f: F[(A, B) => C]): F[C]
	