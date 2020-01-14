# Hoisin 0.1.0 (beta)
A Kotlin jvm library to provide a modern compile time interface with a [JSON RPC](https://www.jsonrpc.org/specification) server. 

### Why the name?
I had pho the day I named it. Thats it. 

We're currenlty in beta and I have no quarms changing the name if you have a better one, feel free to PR one, or send me a [tweet](https://twitter.com/darrellii). If what gets suggested is better, I might change it. 

### Why Beta?
__It's still under development!__ This project mostly is being developed allong side an Android project of mine. 
Because that project is still very new and untested, this project too is very new and very _very_ untested. 

This means future releases of this lib could have api breaking changes(Like a name change). I will do my best to document what changes will possibly break your code, but luckily with the release system you should be able to update at your own pace. 

Either way, if youre using this in your code be sure to follow this repo and follow me on twitter for updates. 

## How to add Hoisin to your project using Gradle
First if you haven't already add [jitpack io](https://jitpack.io/) to your `root/build.gradle`
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Next add Hoisin as a dependancy and you're all done!
```
  dependencies {
		implementation 'com.github.Darrellii:Hoisin:<release-tag>'
  }
```
_**Jobs done!**_

## Usage
As an example we're going to pretend we have a calculator server (boring I know): `www.doMoreMath.com`.

We want to POST to `/calculator` with the `JSON rpc` : `{"jsonrpc": "2.0", "method": "subtract", "params": {"subtrahend": 23, "minuend": 42}, "id": 3}`
Lets assume the result is goig to be an Int, and we can ignore what ever the error is.

``` kotlin
// Using Hoisin
interface Calculator {
  fun subtract(@Param("subtrahend") x:Int, @Param("minuend") y:Int): Call
}

fun main() {
  val hoisin = Hoisin("www.doMoreMath.com")
 
  val calculator = hoisin[Calculator::class.java]
  
  GlobalScope.launch {
    calculator.subtract<Int, Unit>(23, 42).call { answer -> 
      println(answer) // prints what ever the server answers. Lets hope 19.
    }
  }
}
```
Hopefully this enough of an example to get you started! Happy Coding!

## FAQ
_This is awkward. No one has asked anything yet_
In the mean time if you have any questions like I said above feel free to reach out to me on Twitter [@Darrellii](https://twitter.com/darrellii).

Also the app I'm developing along side this library is actually a project I'm developing live one [Twitch](https://twitch.com/darrellii), so feel free to drop me a follow there and help me along the way.


