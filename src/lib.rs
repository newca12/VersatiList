#[macro_use]
extern crate serde_derive;
extern crate serde;
extern crate serde_json;

use std::collections::HashMap;

#[derive(Serialize, Deserialize)]
struct Recipe {
    name: String,
    items: Vec<String>,
}

pub fn load() -> HashMap<String, Vec<String>> {
    let recipes_data = include_str!("data/recipes.json");
    let mut recipes = HashMap::new();
    let recipes_data = LinesWithEndings {
        input: recipes_data,
    };
    for line in recipes_data {
        let deserialized: Recipe = serde_json::from_str(&line).unwrap();
        recipes.insert(deserialized.name, deserialized.items);
    }
    recipes
}
//https://stackoverflow.com/a/40457615
/// Iterator yielding every line in a string. The line includes newline character(s).
pub struct LinesWithEndings<'a> {
    input: &'a str,
}

impl<'a> LinesWithEndings<'a> {
    pub fn from(input: &'a str) -> LinesWithEndings<'a> {
        LinesWithEndings { input: input }
    }
}

impl<'a> Iterator for LinesWithEndings<'a> {
    type Item = &'a str;

    #[inline]
    fn next(&mut self) -> Option<&'a str> {
        if self.input.is_empty() {
            return None;
        }
        let split = self
            .input
            .find('\n')
            .map(|i| i + 1)
            .unwrap_or(self.input.len());
        let (line, rest) = self.input.split_at(split);
        self.input = rest;
        Some(line)
    }
}
