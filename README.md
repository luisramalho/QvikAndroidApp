# Qvik's Sample Android Application

## What?

A sample application that showcases Qvik's android development best practices. Our sample app is intended to be a sort of a bootstrapper application that can be used to get a head start in building android applications.

The sample application includes the following:

* Authentication
* List-Detail view
* Storage
  * Room Persistence Library
  * Realm
  * Cloud (Firebase?)
* Analytics
* …

## Why?

Starting a project from scratch can de daunting, Google has not been too opinionated and that has left the implementation details to the developers themselves. Thus, we at Qvik, would like to offer what we believe to be a good foundation for an android application. Our goal is not to impose this architecture or components used, but be a little bit opinionated as to what we consider is a good infrastructure.

## How?

### Architecture

The architecture we _lightly_ recommend and use at Qvik is the model–view–viewmodel (MVVM):

![Model–view–viewmodel (MVVM)](https://upload.wikimedia.org/wikipedia/commons/8/87/MVVMPattern.png "Model–view–viewmodel (MVVM)")

We opted to build this reference android application using Android's [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel.html) class from the Architecture Components Library due to its ability of being scoped to the android lifecycle.

![The lifecycle of a ViewModel](https://developer.android.com/images/topic/libraries/architecture/viewmodel-lifecycle.png "The lifecycle of a ViewModel")


License
----

The MIT License (MIT)

Copyright (c) 2017 Qvik Oy

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
