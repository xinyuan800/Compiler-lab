; ModuleID = 'module'
source_filename = "module"

@i = global i32 0
@j = global i32 1
@k = global i32 2

define i32 @com(i32 %0, i32 %1) {
comEntry:
  %i = alloca i32, align 4
  store i32 %0, i32* %i, align 4
  %j = alloca i32, align 4
  store i32 %1, i32* %j, align 4
  %i1 = load i32, i32* %i, align 4
  %addtmp = add i32 %i1, 1
  %j2 = load i32, i32* %j, align 4
  %cmptmp = icmp slt i32 %addtmp, %j2
  %zexttmp = zext i1 %cmptmp to i32
  %finalcmp = icmp ne i32 0, %zexttmp
  br i1 %finalcmp, label %true0, label %false0

true0:                                            ; preds = %comEntry
  ret i32 0
  br label %end0

false0:                                           ; preds = %comEntry
  br label %end0

end0:                                             ; preds = %false0, %true0
  ret i32 -1
}

define i32 @main() {
mainEntry:
  %a = alloca i32, align 4
  store i32 1, i32* %a, align 4
  %count = alloca i32, align 4
  store i32 0, i32* %count, align 4
  %a1 = load i32, i32* %a, align 4
  %count2 = load i32, i32* %count, align 4
  %a3 = load i32, i32* %a, align 4
  %returnValue = call i32 @com(i32 %count2, i32 %a3)
  %addtmp = add i32 %a1, %returnValue
  store i32 %addtmp, i32* %count, align 4
  %count4 = load i32, i32* %count, align 4
  %a5 = load i32, i32* %a, align 4
  %returnValue6 = call i32 @com(i32 %count4, i32 %a5)
  ret i32 %returnValue6
}

define i32 @b() {
bEntry:
  %returnValue = call i32 @main()
  ret i32 %returnValue
}
