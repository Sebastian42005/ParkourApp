import SwiftUI

struct SelectDropdown<T: Hashable>: View {
    let options: [T]
    @Binding var selectedItem: T?
    var displayText: (T) -> String
    var placeholder: String

    var body: some View {
        Menu {
            ForEach(options, id: \.self) { option in
                Button(action: {
                    selectedItem = option
                }) {
                    Text(displayText(option))
                }
            }
        } label: {
            HStack {
                Text(selectedItem.map(displayText) ?? placeholder)
                    .foregroundColor(selectedItem == nil ? .gray : .primary)
                Image(systemName: "chevron.down")
                    .foregroundColor(.gray)
            }
            .padding()
            .background(Color.gray.opacity(0.1))
            .cornerRadius(8)
        }
    }
}
