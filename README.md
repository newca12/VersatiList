# VersatiList

### About ###
VersatiList is a multi-platform dynamic list filter

The Scala/JavaFX multi-platform (iOS/Android/Windows/Mac OSX/Linux) implementation has been archived in [scala branch](https://github.com/newca12/VersatiList/tree/scala)

This is now re-written in rust 

A sample dataset is provided to demonstrate recipe search by ingredients you have at home

VersatiList is an EDLA project.

The purpose of [edla.org](http://www.edla.org) is to promote the state of the art in various domains.

### Usage ###
To launch the GUI 
```
cargo run
``` 

### Web version ###
You can try the experimental online version [VersatiList][1]
or built it yourself :  
```
rustup target add wasm32-unknown-unknown
cargo install wasm-bindgen-cli
cargo build --target wasm32-unknown-unknown
wasm-bindgen target/wasm32-unknown-unknown/debug/versatilist.wasm --out-dir VersatiList --web
```

### License ###
© 2016-2021 Olivier ROLAND. Distributed under the GPLv3 License.

[1]: https://edla.org/VersatiList