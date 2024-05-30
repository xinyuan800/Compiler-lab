; ModuleID = 'module'
source_filename = "module"

@i = global i32 0

define i32 @f(i32 %0) {
fEntry:
  %i = alloca i32, align 4
  store i32 %0, i32* %i, align 4
  %i1 = load i32, i32* %i, align 4
  ret i32 %i1
}

define void @print(i32 %0) {
printEntry:
  %x = alloca i32, align 4
  store i32 %0, i32* %x, align 4
  %i = load i32, i32* @i, align 4
  %addtmp = add i32 %i, 1
  store i32 %addtmp, i32* @i, align 4
  ret void
}

define i32 @main() {
mainEntry:
  %a = alloca i32, align 4
  store i32 1, i32* %a, align 4
  %a1 = load i32, i32* %a, align 4
  %returnValue = call i32 @f(i32 %a1)
  ret i32 %returnValue
}
