//
//  ProfileTab.swift
//  ParkourApp
//
//  Created by Sebastian Ederer on 23.10.24.
//

import SwiftUI

struct ProfileTab: View {
    let imageURL = URL(string: "https://my.alfred.edu/zoom/_images/foster-lake.jpg")
    var user = User()
    
    var body: some View {
        VStack(spacing: 15) {
            HStack(spacing: 25) {
                if let imageURL = imageURL {
                    AsyncImage(url: imageURL) { image in
                        image
                            .resizable()
                            .scaledToFill()
                            .frame(width: 90, height: 90)
                            .clipped()
                            .cornerRadius(100)
                    } placeholder: {
                        // Platzhalter anzeigen, während das Bild geladen wird
                        ProgressView()
                            .frame(width: 150, height: 150)
                    }
                } else {
                    // Fehlerbehandlung, falls URL ungültig
                    Text("Bild-URL ungültig")
                }
                
                VStack(alignment: .leading, spacing: 5) {
                    Text(user.username)
                        .font(.title)
                        .frame(alignment: .leading)
                    Text(user.description)
                }
                Spacer()
            }
            
            HStack(spacing: 20) {
                Spacer()
                VStack(alignment: .center, spacing: 5) {
                    
                    Image(systemName: "mappin.and.ellipse")
                        .font(.system(size: 32))
                        .foregroundColor(Color.primary)
                    
                    Text(String(user.spotsAmount))
                        .fontWeight(.bold)
                        .font(.system(size: 20))
                    
                    Text("Spots")
                        .font(.system(size: 20))
                }
                Divider()
                    .frame(height: 70)
                VStack(alignment: .center, spacing: 5) {
                    Image(systemName: "person.fill")
                        .font(.system(size: 40))
                        .foregroundColor(Color.primary)
                    
                    Text(String(user.spotsAmount))
                        .fontWeight(.bold)
                        .font(.system(size: 20))
                    
                    Text("Followers")
                        .font(.system(size: 20))
                }
                Divider()
                    .frame(height: 70)
                VStack(alignment: .center, spacing: 5) {

                    Image(systemName: "person.fill")
                        .font(.system(size: 40))
                        .foregroundColor(Color.primary)
                    
                    Text(String(user.spotsAmount))
                        .fontWeight(.bold)
                        .font(.system(size: 20))
                    
                    Text("Follows")
                        .font(.system(size: 20))
                }
                Spacer()
            }
            .padding(10)
            .background(Color.cardColor)
            .cornerRadius(20)
            .shadow(color: Color.black.opacity(0.2), radius: 10, x: 0, y: 5)
            
            Spacer()
        }.padding(20)
            .background(Color.backgroundColor)
    }
}

#Preview {
    ProfileTab()
}
