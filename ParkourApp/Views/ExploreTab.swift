//
//  ExploreTab.swift
//  ParkourApp
//
//  Created by Sebastian Ederer on 23.10.24.
//

import SwiftUI

struct ExploreTab: View {
    let columns = [
           GridItem(.flexible()),
           GridItem(.flexible())
       ]
    let imageNames = ["bild1", "bild2", "bild3", "bild4", "bild5", "bild6"]
    let imageURL = URL(string: "https://my.alfred.edu/zoom/_images/foster-lake.jpg")

    var body: some View {
        ScrollView {
                    LazyVGrid(columns: columns, spacing: 16) {
                        ForEach(imageNames, id: \.self) { imageName in
                            if let imageURL = imageURL {
                                            AsyncImage(url: imageURL) { image in
                                                image
                                                    .resizable()
                                                    .scaledToFill()
                                                    .frame(width: 150, height: 150)
                                                    .clipped()
                                                    .cornerRadius(10)
                                            } placeholder: {
                                                // Platzhalter anzeigen, während das Bild geladen wird
                                                ProgressView()
                                                    .frame(width: 150, height: 150)
                                            }
                                        } else {
                                            // Fehlerbehandlung, falls URL ungültig
                                            Text("Bild-URL ungültig")
                                        }
                        }
                    }
                    .padding()
                }
    }
}

#Preview {
    ExploreTab()
}
