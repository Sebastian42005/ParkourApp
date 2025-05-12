import SwiftUI

enum SearchType: String, CaseIterable, Identifiable {
    case users = "Users"
    case spots = "Spots"
    
    var id: String { self.rawValue }
}

struct CustomSearchBar: View {
    @Binding var searchText: String
    @Binding var selectedType: SearchType
    var onSearch: () -> Void
    
    var body: some View {
        HStack(spacing: 8) {
            Menu {
                ForEach(SearchType.allCases) { type in
                    Button(action: {
                        selectedType = type
                        onSearch()
                    }) {
                        HStack {
                            Text(type.rawValue.localized())
                            if selectedType == type {
                                Image(systemName: "checkmark")
                            }
                        }
                    }
                }
            } label: {
                Label(selectedType.rawValue.lowercased().localized(), systemImage: "chevron.down")
                    .font(.subheadline)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 6)
                    .background(Color.gray.opacity(0.2))
                    .cornerRadius(8)
            }
            
            TextField("search".localized() + " " + selectedType.rawValue.lowercased().localized().lowercased() + "...", text: $searchText, onCommit: {
                onSearch()
            })
            .textFieldStyle(RoundedBorderTextFieldStyle())
            .autocapitalization(.none)
            .disableAutocorrection(true)
            
            if !searchText.isEmpty {
                Button(action: {
                    searchText = ""
                    onSearch()
                }) {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundColor(.gray)
                }
            }
        }
        .padding(.horizontal)
        .padding(.vertical, 8)
        .background(Color(.systemGray6))
        .cornerRadius(12)
        .padding(.horizontal)
    }
}
